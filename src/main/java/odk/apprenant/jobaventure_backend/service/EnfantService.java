package odk.apprenant.jobaventure_backend.service;


import odk.apprenant.jobaventure_backend.model.Enfant;
import odk.apprenant.jobaventure_backend.model.Parent;
import odk.apprenant.jobaventure_backend.model.Role;
import odk.apprenant.jobaventure_backend.repository.EnfanrRepository;
import odk.apprenant.jobaventure_backend.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class EnfantService {
    @Autowired
    private EnfanrRepository enfanrRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private Enfant getCurrentEnfant() {
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


}
