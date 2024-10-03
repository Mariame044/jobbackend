package odk.apprenant.jobaventure_backend.service;

import odk.apprenant.jobaventure_backend.model.Admin;
import odk.apprenant.jobaventure_backend.model.Enfant;
import odk.apprenant.jobaventure_backend.model.Parent;
import odk.apprenant.jobaventure_backend.model.Role;
import odk.apprenant.jobaventure_backend.repository.EnfanrRepository;
import odk.apprenant.jobaventure_backend.repository.ParentRepository;
import odk.apprenant.jobaventure_backend.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParentService {
    @Autowired
    private ParentRepository parentRepository;
    @Autowired
    private EnfanrRepository enfanrRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private Parent getCurrentParent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Supposons que l'email est utilisé comme principal
        return parentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Administrateur non trouvé"));
    }

    // Méthode pour créer un parent
    public Parent registerParent(Parent parent) {
        // Vérifier si le rôle 'ENFANT' existe déjà
        Role roleParent = roleRepository.findByNom("Parent")
                .orElseGet(() -> {
                    // Si le rôle n'existe pas, le créer
                    Role newRole = new Role();
                    newRole.setNom("Parent");
                    return roleRepository.save(newRole); // Enregistrer le nouveau rôle
                });

        // Assigner le rôle à l'enfant
        parent.setRole(roleParent);

        // Encoder le mot de passe de l'enfant
        parent.setPassword(passwordEncoder.encode(parent.getPassword()));

        // Enregistrer l'enfant dans la base de données
        return parentRepository.save(parent); // Correction de 'enfanrRepository' à 'enfantRepository'
    }

    // Méthode pour récupérer tous les parents
    public List<Parent> obtenirTousLesParents() {
        return parentRepository.findAll();
    }

    // Méthode pour récupérer un parent par son ID
    public Optional<Parent> obtenirParentParId(Long id) {
        return parentRepository.findById(id);
    }

    // Méthode pour mettre à jour un parent
    public Parent mettreAJourParent(Long id, Parent parentDetails) {
        Parent parent = parentRepository.findById(id).orElseThrow(() -> new RuntimeException("Parent non trouvé"));
        parent.setNom(parentDetails.getNom());
        parent.setEmail(parentDetails.getEmail());

        parent.setProfession(parentDetails.getProfession());
        // Autres champs à mettre à jour si nécessaire

        return parentRepository.save(parent);
    }

    // Méthode pour supprimer un parent
    public void supprimerParent(Long id) {
        parentRepository.deleteById(id);
    }
    // Méthode pour assigner un enfant à un parent (Superviser)
    public Parent superviseEnfant(Long parentId, Long enfantId) {
        // Vérifier le parent actuellement authentifié
        Parent currentParent = getCurrentParent();

        // Vérifier si le parent et l'enfant existent
        Optional<Parent> parent = parentRepository.findById(parentId);
        Optional<Enfant> enfant = enfanrRepository.findById(enfantId);

        if (parent.isPresent() && enfant.isPresent()) {
            Enfant enfantExistant = enfant.get();
            enfantExistant.setParent(parent.get()); // Assigner le parent à l'enfant
            enfanrRepository.save(enfantExistant); // Sauvegarder l'enfant avec le parent assigné

            return parent.get();
        } else {
            throw new RuntimeException("Parent ou Enfant non trouvé");
        }
    }

    // Méthode pour récupérer tous les enfants supervisés par un parent
    public List<Enfant> getEnfantsByParent(Long parentId) {
        // Vérifier le parent actuellement authentifié
        Parent currentParent = getCurrentParent();

        // Récupérer le parent par son ID
        Optional<Parent> parent = parentRepository.findById(parentId);

        if (parent.isPresent()) {
            return parent.get().getEnfant(); // Retourner la liste des enfants supervisés
        } else {
            throw new RuntimeException("Parent non trouvé");
        }
    }
}

