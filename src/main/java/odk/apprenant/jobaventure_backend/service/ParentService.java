package odk.apprenant.jobaventure_backend.service;

import odk.apprenant.jobaventure_backend.model.Parent;
import odk.apprenant.jobaventure_backend.model.Role;
import odk.apprenant.jobaventure_backend.repository.ParentRepository;
import odk.apprenant.jobaventure_backend.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParentService {
    @Autowired
    private ParentRepository parentRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Méthode pour créer un parent
    public Parent registerParent(Parent parent) {
        // Rechercher le rôle 'PARENT'
        Role roleParent = roleRepository.findByNom("Parent")
                .orElseThrow(() -> new RuntimeException("Rôle 'PARENT' non trouvé."));
        parent.setRole(roleParent); // Assigner le rôle
        parent.setPassword(passwordEncoder.encode(parent.getPassword())); // Encoder le mot de passe
        return parentRepository.save(parent);
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
}

