package odk.apprenant.jobaventure_backend.service;

import odk.apprenant.jobaventure_backend.dtos.CategorieDto;
import odk.apprenant.jobaventure_backend.model.Admin;
import odk.apprenant.jobaventure_backend.model.Categorie;
import odk.apprenant.jobaventure_backend.repository.AdminRepository;
import odk.apprenant.jobaventure_backend.repository.CategorieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategorieService {

    @Autowired
    private CategorieRepository categorieRepository;



    @Autowired
    private AdminRepository adminRepository;

    // Méthode pour obtenir l'administrateur connecté
    private Admin getCurrentAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Ici, le principal est l'email
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Administrateur non trouvé"));
    }

    // Récupérer toutes les catégories
    public List<CategorieDto> getAllCategories() {
        //getCurrentAdmin();
        return categorieRepository.findAll()
                .stream()
                .map(this::convertToDto)  // Convertir l'entité en DTO
                .collect(Collectors.toList());
    }

    // Récupérer une catégorie par ID
    public Optional<CategorieDto> getCategorieById(Long id) {
        //getCurrentAdmin();
        return categorieRepository.findById(id)
                .map(this::convertToDto);  // Convertir l'entité en DTO
    }

    // Créer une nouvelle catégorie à partir du DTO
    public CategorieDto createCategorie(CategorieDto categorieDto) {
        Categorie categorie = new Categorie();
        categorie.setNom(categorieDto.getNom());

        // Associer l'administrateur connecté à la catégorie
        Admin admin = getCurrentAdmin();
        categorie.setAdmin(admin); // Enregistrer l'administrateur connecté comme créateur de la catégorie

        Categorie savedCategorie = categorieRepository.save(categorie);
        return convertToDto(savedCategorie);  // Retourner le DTO de la catégorie créée
    }

    // Mettre à jour une catégorie
    public CategorieDto updateCategorie(Long id, CategorieDto updatedCategorieDto) {
        getCurrentAdmin();
        return categorieRepository.findById(id).map(categorie -> {
            categorie.setNom(updatedCategorieDto.getNom());
            Categorie updatedCategorie = categorieRepository.save(categorie);
            return convertToDto(updatedCategorie);  // Retourner le DTO de la catégorie mise à jour
        }).orElseThrow(() -> new RuntimeException("Categorie not found with id " + id));
    }

    // Supprimer une catégorie
    public void deleteCategorie(Long id) {
        categorieRepository.deleteById(id);
    }

    // Méthode de conversion de l'entité Categorie en DTO
    private CategorieDto convertToDto(Categorie categorie) {
        CategorieDto dto = new CategorieDto();
        dto.setId((long) categorie.getId());
        dto.setNom(categorie.getNom());
        return dto;
    }

    // Ajoutez la méthode convertToEntity
    public Categorie convertToEntity(CategorieDto categorieDto) {
        Categorie categorie = new Categorie();
        if (categorieDto.getId() != null) {
            // Conversion de Long en int
            categorie.setId(categorieDto.getId().intValue());  // Utiliser intValue() si ID est un Long
        }
        categorie.setNom(categorieDto.getNom());

        // Assurez-vous d'ajouter d'autres attributs de Categorie si nécessaire
        return categorie;
    }

}