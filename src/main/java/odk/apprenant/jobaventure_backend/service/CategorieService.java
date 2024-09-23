package odk.apprenant.jobaventure_backend.service;

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

    public List<Categorie> getAllCategories() {
        return categorieRepository.findAll();
    }

    public Optional<Categorie> getCategorieById(Long id) {
        return categorieRepository.findById(id);
    }

    public Categorie createCategorie(Categorie categorie) {
        // Associer l'administrateur connecté à la catégorie
        Admin admin = getCurrentAdmin();
        categorie.setAdmin(admin); // Enregistrer l'administrateur connecté comme créateur de la catégorie

        return categorieRepository.save(categorie);
    }

    public Categorie updateCategorie(Long id, Categorie updatedCategorie) {
        return categorieRepository.findById(id).map(categorie -> {
            categorie.setNom(updatedCategorie.getNom());
            return categorieRepository.save(categorie);
        }).orElseThrow(() -> new RuntimeException("Categorie not found with id " + id));
    }

    public void deleteCategorie(Long id) {
        categorieRepository.deleteById(id);
    }
}