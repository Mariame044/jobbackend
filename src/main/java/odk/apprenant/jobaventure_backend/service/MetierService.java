package odk.apprenant.jobaventure_backend.service;


import odk.apprenant.jobaventure_backend.model.Admin;
import odk.apprenant.jobaventure_backend.model.Metier;
import odk.apprenant.jobaventure_backend.repository.AdminRepository;
import odk.apprenant.jobaventure_backend.repository.MetierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class MetierService {


    @Autowired
    private MetierRepository metierRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private FileStorageService fileStorageService;

    // Méthode pour obtenir l'administrateur connecté
    private Admin getCurrentAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Supposons que l'email est utilisé comme principal
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Administrateur non trouvé"));
    }

    public Metier creerMetier(Metier metier, MultipartFile image) throws IOException {
        if (metier.getCategorie() == null) {
            throw new RuntimeException("La catégorie est obligatoire.");
        }

        // Validation et sauvegarde de l'image

        if (image != null && !image.isEmpty()) {
            String imageUrl = fileStorageService.sauvegarderImage(image);
            metier.setImageUrl(imageUrl);
        }
        // Associer l'administrateur connecté au métier
        Admin admin = getCurrentAdmin();
        metier.setAdmin(admin); // Enregi
        return metierRepository.save(metier);
    }

    public Metier modifierMetier(Long id, Metier detailsMetier, MultipartFile image) throws IOException {
        Optional<Metier> optionalMetier = metierRepository.findById(id);

        if (optionalMetier.isPresent()) {
            Metier metier = optionalMetier.get();
            metier.setNom(detailsMetier.getNom());
            metier.setDescription(detailsMetier.getDescription());

            // Gestion de l'image
            if (image != null && !image.isEmpty()) {
                // Supprimer l'ancienne image si elle existe
                if (metier.getImageUrl() != null) {
                    Files.deleteIfExists(Paths.get(metier.getImageUrl()));
                }
                String imageUrl = fileStorageService.sauvegarderImage(image);
                metier.setImageUrl(imageUrl);
            }

            return metierRepository.save(metier);
        } else {
            throw new RuntimeException("Le métier avec l'ID " + id + " n'existe pas.");
        }
    }

    public Metier getMetier(Long id) {
        return metierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Le métier avec l'ID " + id + " n'existe pas."));
    }

    public void supprimerMetier(Long id) throws IOException {
        Metier metier = getMetier(id);
        // Supprimer l'image si elle existe
        if (metier.getImageUrl() != null) {
            Files.deleteIfExists(Paths.get(metier.getImageUrl()));
        }
        metierRepository.delete(metier);
    }

}