package odk.apprenant.jobaventure_backend.service;


import odk.apprenant.jobaventure_backend.dtos.CategorieDto;
import odk.apprenant.jobaventure_backend.dtos.MetierDto;
import odk.apprenant.jobaventure_backend.exeption.MetierNotFoundException;
import odk.apprenant.jobaventure_backend.model.Admin;
import odk.apprenant.jobaventure_backend.model.Categorie;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MetierService {


    @Autowired
    private MetierRepository metierRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private CategorieService categorieService;

    // Méthode pour obtenir l'administrateur connecté
    private Admin getCurrentAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Ici, le principal est l'email
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Administrateur non trouvé"));
    }
    // Mise à jour de cette méthode pour retourner une liste de MetierDto
    public List<Metier> getAllMetiers() {
       // getCurrentAdmin();
        return metierRepository.findAll();

    }
    

    // Créer un métier
    // Create a métier
    public MetierDto creerMetier(MetierDto metierDto, MultipartFile image) throws IOException {
        Admin currentAdmin = getCurrentAdmin(); // Get the connected admin
        if (metierDto.getCategorie() == null) {
            throw new RuntimeException("La catégorie est obligatoire.");
        }

        Metier metier = new Metier();
        metier.setNom(metierDto.getNom());
        metier.setDescription(metierDto.getDescription());
        metier.setAdmin(currentAdmin); // Set the connected admin

        // Image management
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileStorageService.sauvegarderImage(image);
            metier.setImageUrl(imageUrl);
        }

        // Retrieve category from DTO
        Optional<CategorieDto> categorieDtoOpt = categorieService.getCategorieById(metierDto.getCategorie().getId());
        if (categorieDtoOpt.isPresent()) {
            metier.setCategorie(categorieService.convertToEntity(categorieDtoOpt.get()));
        }

        Metier savedMetier = metierRepository.save(metier);
        return convertToDto(savedMetier);
    }


    public MetierDto modifierMetier(Long id, MetierDto detailsMetierDto, MultipartFile image) throws IOException {
        Optional<Metier> optionalMetier = metierRepository.findById(id);

        if (optionalMetier.isPresent()) {
            Metier metier = optionalMetier.get();
            metier.setNom(detailsMetierDto.getNom());
            metier.setDescription(detailsMetierDto.getDescription());

            // Gestion de l'image
            if (image != null && !image.isEmpty()) {
                // Supprimer l'ancienne image si elle existe
                if (metier.getImageUrl() != null) {
                    Files.deleteIfExists(Paths.get(metier.getImageUrl()));
                }
                String imageUrl = fileStorageService.sauvegarderImage(image);
                metier.setImageUrl(imageUrl);
            }

            // Sauvegarde du métier modifié
            Metier updatedMetier = metierRepository.save(metier);
            return convertToDto(updatedMetier); // Retourne MetierDto
        } else {
            throw new MetierNotFoundException("Le métier avec l'ID " + id + " n'existe pas.");
        }
    }

    public MetierDto getMetier(Long id) {
        Metier metier = metierRepository.findById(id)
                .orElseThrow(() -> new MetierNotFoundException("Le métier avec l'ID " + id + " n'existe pas."));
        return convertToDto(metier); // Retourne MetierDto
    }

    // Supprimer une catégorie
    public void deleteMetier(Long id) {
        metierRepository.deleteById(id);
    }

    // Méthode pour convertir Metier en MetierDto
    private MetierDto convertToDto(Metier metier) {
        MetierDto metierDto = new MetierDto();
        metierDto.setId(metier.getId());
        metierDto.setNom(metier.getNom());
        metierDto.setDescription(metier.getDescription());
        metierDto.setImageUrl(metier.getImageUrl());

        // Optionnel: Vous pouvez ajouter d'autres conversions pour les associations
        if (metier.getAdmin() != null) {
            Admin admin = metier.getAdmin(); // Obtenir l'objet Admin directement
            // Créer un DTO pour Admin si nécessaire
            Admin adminDto = new Admin();
            adminDto.setId(admin.getId());
            adminDto.setEmail(admin.getEmail());
            // Ajoutez d'autres attributs si nécessaire
            //metierDto.setAdmin(adminDto);
        }

        // Ici, vous pouvez également peupler les autres listes d'IDs (quizIds, videoIds, etc.) si nécessaire
        return metierDto;
    }

}