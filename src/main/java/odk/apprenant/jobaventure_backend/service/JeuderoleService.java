package odk.apprenant.jobaventure_backend.service;


import odk.apprenant.jobaventure_backend.model.*;
import odk.apprenant.jobaventure_backend.repository.AdminRepository;
import odk.apprenant.jobaventure_backend.repository.EnfanrRepository;
import odk.apprenant.jobaventure_backend.repository.JeuderoleRepository;
import odk.apprenant.jobaventure_backend.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@Service
public class JeuderoleService {

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private JeuderoleRepository jeuderoleRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private EnfanrRepository enfanrRepository;


    // Méthode pour obtenir l'administrateur connecté
    private Admin getCurrentAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Supposons que l'email est utilisé comme principal
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Administrateur non trouvé"));
    }

    public Jeuderole ajouterJeuDeRole(Jeuderole jeuderole, MultipartFile image) throws IOException {
        if (jeuderole.getMetier() == null) {
            throw new RuntimeException("La catégorie est obligatoire.");
        }
        //if (jeuderole.getQuestion() == null || jeuderole.getQuestion().isEmpty()) {
           // throw new RuntimeException("Les questions sont obligatoires.");
        //}

        String cheminImage = fileStorageService.sauvegarderImage(image);
        jeuderole.setImageUrl(cheminImage); // Définit l'URL ou le chemin de la vidéo
        Admin admin = getCurrentAdmin();
        jeuderole.setAdmin(admin); // Enregi
        return jeuderoleRepository.save(jeuderole);

    }

    public Jeuderole modifierJeuDeRole(Long id, Jeuderole jeuderole) {
        Optional<Jeuderole> jeuOptional = jeuderoleRepository.findById(id);
        if (jeuOptional.isPresent()) {
            Jeuderole jeuExist = jeuOptional.get();
            jeuExist.setNom(jeuderole.getNom());
            jeuExist.setDescription(jeuderole.getDescription());
            //jeuExist.setQuestion(jeuderole.getQuestion()); // Mise à jour des questions
            return jeuderoleRepository.save(jeuExist);
        } else {
            throw new RuntimeException("Le jeu de rôle avec l'ID " + id + " n'existe pas.");
        }
    }

    public void supprimerJeuDeRole(Long id) {
        if (jeuderoleRepository.existsById(id)) {
            jeuderoleRepository.deleteById(id);
        } else {
            throw new RuntimeException("Le jeu de rôle avec l'ID " + id + " n'existe pas.");
        }
    }

    public Jeuderole getJeuDeRoleDetails(Long jeuId) {
        return jeuderoleRepository.findById(jeuId)
                .orElseThrow(() -> new RuntimeException("Le jeu de rôle avec l'ID " + jeuId + " n'existe pas."));
    }

    public String verifierReponse(Long enfantId, Long jeuId, Long questionId, String reponseDonnee) {
        Enfant enfant = enfanrRepository.findById(enfantId)
                .orElseThrow(() -> new RuntimeException("L'enfant avec l'ID " + enfantId + " n'existe pas."));

        // Vérifier si l'enfant est en attente
        if (enfant.isEnAttente()) {
            long tempsRestant = 4 * 60 * 60 * 1000 - (new Date().getTime() - enfant.getDerniereTentative().getTime());
            if (tempsRestant > 0) {
                long heuresRestantes = tempsRestant / (1000 * 60 * 60);
                return "Vous devez attendre " + heuresRestantes + " heures avant de pouvoir jouer à nouveau.";
            } else {
                enfant.setEnAttente(false); // L'enfant peut jouer à nouveau
                enfant.setTentativesRestantes(3); // Réinitialiser les tentatives
            }
        }

        Jeuderole jeuderole = getJeuDeRoleDetails(jeuId);
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("La question avec l'ID " + questionId + " n'existe pas."));

        // Vérifier la réponse
        if (question.getReponse() != null && question.getReponse().getReponsepossible().contains(reponseDonnee)) {
            if (question.getReponse().getCorrect()) {
                enfant.setTentativesRestantes(3); // Réinitialiser les tentatives
                enfanrRepository.save(enfant);
                return "Réponse correcte! Vous avez gagné des points.";
            } else {
                enfant.setTentativesRestantes(enfant.getTentativesRestantes() - 1);
                enfant.setDerniereTentative(new Date()); // Mettre à jour la dernière tentative
                if (enfant.getTentativesRestantes() <= 0) {
                    enfant.setEnAttente(true); // Passer en mode attente
                    enfanrRepository.save(enfant);
                    return "Tentatives épuisées! Vous devez attendre 4 heures avant de pouvoir jouer à nouveau.";
                }
                enfanrRepository.save(enfant);
                return "Réponse incorrecte. Tentatives restantes : " + enfant.getTentativesRestantes();
            }
        }
        return "La réponse donnée n'est pas valide.";
    }

    public int calculerScore(Long enfantId, Long jeuId, Map<Long, String> reponsesDonnees) {
        int scoreTotal = 0;
        int bonnesReponses = 0;

        for (Map.Entry<Long, String> entry : reponsesDonnees.entrySet()) {
            Long questionId = entry.getKey();
            String reponseDonnee = entry.getValue();

            String resultat = verifierReponse(enfantId, jeuId, questionId, reponseDonnee);
            if ("Réponse correcte!".equals(resultat)) {
                Question question = questionRepository.findById(questionId)
                        .orElseThrow(() -> new RuntimeException("Question non trouvée"));
                scoreTotal += question.getPoint();
                bonnesReponses++;
            }
        }

        // Calculer le pourcentage de bonnes réponses
        double pourcentage = ((double) bonnesReponses / reponsesDonnees.size()) * 100;

        // Vérifier si l'enfant a gagné un badge
        if (pourcentage >= 80) {
            // Logique pour attribuer un badge à l'enfant
            // Ajoutez ici la logique de mise à jour des badges
            return scoreTotal; // Retourne le score total
        }

        return scoreTotal; // Retourne le score total
    }


    public List<Jeuderole> getAllJeuDeRole() {
        return jeuderoleRepository.findAll(); // Fetch all Jeuderole instances
    }

}