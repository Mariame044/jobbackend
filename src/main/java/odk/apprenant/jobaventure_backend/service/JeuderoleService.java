package odk.apprenant.jobaventure_backend.service;


import odk.apprenant.jobaventure_backend.model.*;
import odk.apprenant.jobaventure_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

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
    @Autowired
    private StatistiqueService statistiqueService;



    // Méthode pour obtenir l'administrateur connecté
    private Admin getCurrentAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Supposons que l'email est utilisé comme principal
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Administrateur non trouvé"));
    }
    private Enfant getCurrentEnfant() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Supposons que l'email est utilisé comme principal

        // Rechercher l'enfant par son email
        return enfanrRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("L'enfant avec l'email " + email + " n'a pas été trouvé"));
    }
    public List<Jeuderole> trouverVideosPourEnfantParAge() {
        Enfant enfant = getCurrentEnfant(); // Récupère l'enfant connecté
        int ageEnfant = enfant.getAge();    // Récupère l'âge de l'enfant

        // Récupère toutes les vidéos
        List<Jeuderole> toutesLesJeuderole = jeuderoleRepository.findAll();
        List<Jeuderole> jeuderolesFiltrees = new ArrayList<>();

        // Filtrer les vidéos par tranche d'âge
        for (Jeuderole jeuderole : toutesLesJeuderole) {
            if (jeuderole.getTrancheage() != null) {
                int ageMin = jeuderole.getTrancheage().getAgeMin();
                int ageMax = jeuderole.getTrancheage().getAgeMax();
                if (ageEnfant >= ageMin && ageEnfant <= ageMax) {
                    jeuderolesFiltrees.add(jeuderole);
                }
            }
        }
        return jeuderolesFiltrees; // Retourne les vidéos filtrées
    }


    public Jeuderole ajouterJeuDeRole(Jeuderole jeuderole, MultipartFile image, MultipartFile audio) throws IOException {
        if (jeuderole.getMetier() == null) {
            throw new RuntimeException("La catégorie est obligatoire.");
        }
        if (jeuderole.getTrancheage() == null) {
            throw new RuntimeException("La tranche age est obligatoire.");
        }
        //if (jeuderole.getQuestion() == null || jeuderole.getQuestion().isEmpty()) {
           // throw new RuntimeException("Les questions sont obligatoires.");
        //}
        // Sauvegarde de l'image
        String cheminImage = fileStorageService.sauvegarderImage(image);
        jeuderole.setImageUrl(cheminImage); // Définit l'URL ou le chemin de la vidéo
        // Sauvegarde de l'audio
        String cheminAudio = fileStorageService.sauvegarderAudio(audio);
        jeuderole.setAudioUrl(cheminAudio); // Ajoute l'audio au jeu de rôle
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

    public List<Question> jouer(Long jeuId) {
        // Vérifiez si le jeu de rôle existe
        Jeuderole jeuderole = getJeuDeRoleDetails(jeuId); // Cette méthode doit être définie pour récupérer les détails du jeu

        // Récupérer les questions liées à ce jeu de rôle
        List<Question> questions = questionRepository.findByJeuderoleId(jeuId);

        // Vérifiez si des questions sont disponibles
        if (questions.isEmpty()) {
            throw new RuntimeException("Aucune question disponible pour le jeu de rôle avec l'ID " + jeuId);
        }

        return questions; // Retourne la liste des questions
    }

    public String verifierReponse(Long jeuId, Long questionId, String reponseDonnee) {
        Enfant enfant = getCurrentEnfant(); // Récupérer l'enfant connecté

        // Vérifier si l'enfant est en attente
        if (enfant.isEnAttente()) {
            long tempsRestant = 4 * 60 * 60 * 1000 - (new Date().getTime() - enfant.getDerniereTentative().getTime());
            if (tempsRestant > 0) {
                long heuresRestantes = tempsRestant / (1000 * 60 * 60);
                return "Vous devez attendre " + heuresRestantes + " heures avant de pouvoir jouer à nouveau.";
            } else {
                enfant.setEnAttente(false); // L'enfant peut jouer à nouveau
                enfant.setTentativesRestantes(6); // Réinitialiser les tentatives
                enfanrRepository.save(enfant); // Sauvegarder l'état de l'enfant
            }
        }

        Jeuderole jeuderole = getJeuDeRoleDetails(jeuId);
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("La question avec l'ID " + questionId + " n'existe pas."));

        // Vérifier si l'enfant a déjà répondu correctement à cette question
        if (enfant.hasResolvedQuestion(questionId)) {
            return "Vous avez déjà répondu correctement à cette question. Aucun point ne sera attribué.";

        }

        // Vérifier la réponse donnée
        if (question.getReponse() != null && question.getReponse().getReponsepossible().contains(reponseDonnee)) {
            if (question.getReponse().getCorrect().equals(reponseDonnee)) {
                int pointsGagnes = question.getPoint();
                enfant.addScore(pointsGagnes); // Ajouter les points au score de l'enfant
                enfant.addQuestionResolue(questionId); // Marquer la question comme résolue
                enfant.setTentativesRestantes(6); // Réinitialiser les tentatives après une bonne réponse
                enfant.setDerniereTentative(new Date()); // Mettre à jour la dernière tentative
                enfanrRepository.save(enfant); // Sauvegarder les changements

                return "Réponse correcte! Vous avez gagné " + pointsGagnes + " points. Score total : " + enfant.getScore();
            } else {
                enfant.setTentativesRestantes(enfant.getTentativesRestantes() - 1);
                enfant.setDerniereTentative(new Date()); // Mettre à jour la dernière tentative
                enfanrRepository.save(enfant); // Sauvegarder les tentatives restantes mises à jour

                if (enfant.getTentativesRestantes() <= 0) {
                    enfant.setEnAttente(true); // Passer en mode attente
                    enfant.setDerniereTentative(new Date()); // Mettre la date actuelle de la tentative échouée
                    enfanrRepository.save(enfant); // Sauvegarder avec le statut en attente
                    return "Tentatives épuisées! Vous devez attendre 4 heures avant de pouvoir jouer à nouveau.";
                }
                return "Réponse incorrecte. Tentatives restantes : " + enfant.getTentativesRestantes();
            }
        }
        return "La réponse donnée n'est pas valide.";
    }


    public int calculerScore(Long jeuId, Map<Long, String> reponsesDonnees) {
        Enfant enfant = getCurrentEnfant(); // Récupérer l'enfant connecté
        int scoreTotal = 0;
        int bonnesReponses = 0;

        // Parcourir toutes les réponses données par l'enfant pour ce jeu
        for (Map.Entry<Long, String> entry : reponsesDonnees.entrySet()) {
            Long questionId = entry.getKey();
            String reponseDonnee = entry.getValue();

            // Vérifier si la réponse donnée est correcte
            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new RuntimeException("Question non trouvée"));

            // Si la réponse est correcte, on incrémente le score
            if (question.getReponse().getCorrect().equals(reponseDonnee)) {
                scoreTotal += question.getPoint(); // Ajouter les points de la question
                bonnesReponses++;
            }
        }

        // Calcul du pourcentage de bonnes réponses
        double pourcentage = ((double) bonnesReponses / reponsesDonnees.size()) * 100;

        // Attribuer un badge si le pourcentage est >= 80%
        if (pourcentage >= 80) {
            Badge badge = new Badge();
            badge.setNom("Badge d'excellence");  // Exemple de nom de badge
            enfant.getBadge().add(badge);        // Ajouter le badge à l'enfant
        }

        // Mise à jour du score total de l'enfant
        enfant.setScore(enfant.getScore() + scoreTotal); // Ajouter le score du jeu au score total

        // Sauvegarder l'enfant avec le nouveau score et les badges
        enfanrRepository.save(enfant);

        return scoreTotal; // Retourner le score total obtenu dans ce jeu
    }


    public List<Jeuderole> getAllJeuDeRole() {
        return jeuderoleRepository.findAll(); // Fetch all Jeuderole instances
    }
    public List<Jeuderole> trouverJeuderoleParMetierEtAge(Long metierId) {
        // Récupérer l'enfant connecté
        Enfant enfant = getCurrentEnfant();
        int ageEnfant = enfant.getAge();

        // Récupérer toutes les vidéos par métier
        List<Jeuderole> jeuderolesParMetier = jeuderoleRepository.findByMetierId(metierId);
        List<Jeuderole> jeuderolesFiltrees = new ArrayList<>();

        // Filtrer les vidéos en fonction de la tranche d'âge de l'enfant
        for (Jeuderole jeuderole : jeuderolesParMetier) {
            if (jeuderole.getTrancheage() != null) {
                int ageMin = jeuderole.getTrancheage().getAgeMin();
                int ageMax = jeuderole.getTrancheage().getAgeMax();
                if (ageEnfant >= ageMin && ageEnfant <= ageMax) {
                    jeuderolesFiltrees.add(jeuderole);
                }
            }
        }

        return jeuderolesFiltrees; // Retourne les vidéos filtrées par métier et tranche d'âge
    }
}