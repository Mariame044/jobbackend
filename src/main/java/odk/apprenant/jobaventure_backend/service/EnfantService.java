package odk.apprenant.jobaventure_backend.service;


import odk.apprenant.jobaventure_backend.model.Enfant;
import odk.apprenant.jobaventure_backend.model.Role;
import odk.apprenant.jobaventure_backend.repository.EnfanrRepository;
import odk.apprenant.jobaventure_backend.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class EnfantService {
    @Autowired
    private EnfanrRepository enfanrRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private  BCryptPasswordEncoder passwordEncoder;

    public Enfant registerEnfant(Enfant enfant) {
        // Rechercher le rôle 'ENFANT'
        Role roleEnfant = roleRepository.findByNom("ENFANT")
                .orElseThrow(() -> new RuntimeException("Rôle 'ENFANT' non trouvé."));
        enfant.setRole(roleEnfant); // Assigner le rôle
        enfant.setPassword(passwordEncoder.encode(enfant.getPassword())); // Encoder le mot de passe
        return enfanrRepository.save(enfant);
    }

}
