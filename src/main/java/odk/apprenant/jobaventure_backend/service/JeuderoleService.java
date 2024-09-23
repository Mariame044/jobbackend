package odk.apprenant.jobaventure_backend.service;


import odk.apprenant.jobaventure_backend.model.Admin;
import odk.apprenant.jobaventure_backend.model.Jeuderole;
import odk.apprenant.jobaventure_backend.model.Metier;
import odk.apprenant.jobaventure_backend.model.Question;
import odk.apprenant.jobaventure_backend.repository.AdminRepository;
import odk.apprenant.jobaventure_backend.repository.JeuderoleRepository;
import odk.apprenant.jobaventure_backend.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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

        // Validation et sauvegarde de l'image

        if (image != null && !image.isEmpty()) {
            String imageUrl = fileStorageService.sauvegarderImage(image);
            jeuderole.setImageUrl(imageUrl);
        }

        return jeuderoleRepository.save(jeuderole);
    }
    public Jeuderole modifierJeuDeRole(Long id, Jeuderole jeuderole) {
        Optional<Jeuderole> jeuOptional = jeuderoleRepository.findById(id);
        if (jeuOptional.isPresent()) {
            Jeuderole jeuExist = jeuOptional.get();
            jeuExist.setNom(jeuderole.getNom());
            jeuExist.setDescription(jeuderole.getDescription());
            jeuExist.setQuestions(jeuderole.getQuestions());
            return jeuderoleRepository.save(jeuExist);
        } else {
            throw new RuntimeException("Le jeu de rôle avec l'ID " + id + " n'existe pas.");
        }
    }
    // Supprimer un jeu de rôle
    public void supprimerJeuDeRole(Long id) {
        if (jeuderoleRepository.existsById(id)) {
            jeuderoleRepository.deleteById(id);
        } else {
            throw new RuntimeException("Le jeu de rôle avec l'ID " + id + " n'existe pas.");
        }
    }

    // Jouer au jeu et récupérer les questions
    public List<Question> jouer(Long jeuId) {
        Optional<Jeuderole> jeuOptional = jeuderoleRepository.findById(jeuId);
        if (jeuOptional.isPresent()) {
            return jeuOptional.get().getQuestions();
        } else {
            throw new RuntimeException("Le jeu de rôle avec l'ID " + jeuId + " n'existe pas.");
        }
    }
}
