package odk.apprenant.jobaventure_backend.service;


import jakarta.transaction.Transactional;
import odk.apprenant.jobaventure_backend.model.*;
import odk.apprenant.jobaventure_backend.repository.EnfanrRepository;
import odk.apprenant.jobaventure_backend.repository.MetierRepository;
import odk.apprenant.jobaventure_backend.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EnfantService {
    @Autowired
    private EnfanrRepository enfanrRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MetierRepository metierRepository;


    public Enfant getCurrentEnfant() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Supposons que l'email est utilisé comme principal
        return enfanrRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Administrateur non trouvé"));
    }

    public Enfant registerEnfant(Enfant enfant) {
        // Vérifier si le rôle 'ENFANT' existe déjà
        Role roleEnfant = roleRepository.findByNom("Enfant")
                .orElseGet(() -> {
                    // Si le rôle n'existe pas, le créer
                    Role newRole = new Role();
                    newRole.setNom("Enfant");
                    return roleRepository.save(newRole); // Enregistrer le nouveau rôle
                });

        // Assigner le rôle à l'enfant
        enfant.setRole(roleEnfant);

        // Encoder le mot de passe de l'enfant
        enfant.setPassword(passwordEncoder.encode(enfant.getPassword()));

        // Enregistrer l'enfant dans la base de données
        return enfanrRepository.save(enfant); // Correction de 'enfanrRepository' à 'enfantRepository'
    }
    @Transactional
    public void mettreAJourScore(Enfant enfant) {
        enfanrRepository.save(enfant); // Cela doit être transactionnel
    }
    @Transactional
    public Map<String, Object> getProgression() {
        Enfant enfantConnecte = getCurrentEnfant();

        // Initialisation des collections

        enfantConnecte.getQuiz().size(); // Chargez les quiz
        enfantConnecte.getVideosRegardees().size(); // Chargez les vidéos
        enfantConnecte.getJeuderole().size(); // Chargez les jeux de rôle
        enfantConnecte.getInterviewregardees().size(); // Chargez les interviews

        // Journalisation des informations de l'enfant
        System.out.println("Nom de l'enfant: " + enfantConnecte.getNom());
        System.out.println("Age de l'enfant: " + enfantConnecte.getAge());
        System.out.println("Score de l'enfant: " + enfantConnecte.getScore());
        System.out.println("Tentatives restantes: " + enfantConnecte.getTentativesRestantes());
        System.out.println("Questions résolues: " + enfantConnecte.getQuestionsResolues());

        System.out.println("Jeux de rôle: " + enfantConnecte.getJeuderole());
        System.out.println("Vidéos: " + enfantConnecte.getVideosRegardees());
        System.out.println("Interviews: " + enfantConnecte.getInterviewregardees());

        // Construire la réponse
        Map<String, Object> progression = new HashMap<>();
        progression.put("nom", enfantConnecte.getNom());
        progression.put("age", enfantConnecte.getAge());
        progression.put("score", enfantConnecte.getScore());
        progression.put("tentativesRestantes", enfantConnecte.getTentativesRestantes());
        progression.put("questionsResolues", enfantConnecte.getQuestionsResolues());

        progression.put("jeuxDeRole", enfantConnecte.getJeuderole());
        progression.put("videos", enfantConnecte.getVideosRegardees());
        progression.put("interviews", enfantConnecte.getInterviewregardees());

        return progression;
    }

    public List<Jeuderole> getJeuxForCurrentEnfant() {
        Enfant enfantConnecte = getCurrentEnfant();  // Récupérer l'enfant connecté

        // S'assurer que les jeux de rôle sont bien chargés depuis la base de données
        List<Jeuderole> jeuxDeRole = enfantConnecte.getJeux();

        // Vérifier si des jeux sont associés à l'enfant
        if (jeuxDeRole.isEmpty()) {
            System.out.println("Aucun jeu de rôle trouvé pour l'enfant: " + enfantConnecte.getNom());
        } else {
            System.out.println("Jeux de rôle pour l'enfant: " + enfantConnecte.getNom());
            jeuxDeRole.forEach(jeu -> System.out.println("Jeu: " + jeu.getNom()));
        }

        return jeuxDeRole;  // Retourner la liste des jeux de rôle
    }



}
