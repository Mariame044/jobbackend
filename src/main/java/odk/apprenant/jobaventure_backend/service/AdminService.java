package odk.apprenant.jobaventure_backend.service;


import jakarta.annotation.PostConstruct;
import odk.apprenant.jobaventure_backend.model.Admin;
import odk.apprenant.jobaventure_backend.model.Professionnel;
import odk.apprenant.jobaventure_backend.model.Role;
import odk.apprenant.jobaventure_backend.model.User;
import odk.apprenant.jobaventure_backend.repository.AdminRepository;
import odk.apprenant.jobaventure_backend.repository.ProfessionnelRepositoty;
import odk.apprenant.jobaventure_backend.repository.RoleRepository;
import odk.apprenant.jobaventure_backend.repository.UserRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final BCryptPasswordEncoder passwordEncoder;
    @Autowired

    private AdminRepository adminRepository;
    @Autowired
    private ProfessionnelRepositoty professionnelRepositoty;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired  // Assurez-vous que l'annotation @Autowired est présente
    private UserRespository userRespository;


    public AdminService(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    // Méthode pour obtenir l'administrateur connecté
    private Admin getCurrentAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Supposons que l'email est utilisé comme principal
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Administrateur non trouvé"));
    }


    public List<Role> lireRoleTypes() {
        return List.of();
    }


    public String ajouterRoleType(Role role) {
        getCurrentAdmin();
        roleRepository.save(role);
        return "Role ajouté avec succès!";
    }

    public String modifierRoleType(Long id, Role roleTypeDetails) {
        getCurrentAdmin();
        return roleRepository.findById(id)
                .map(roleType -> {
                    roleType.setNom(roleTypeDetails.getNom());
                    roleRepository.save(roleType);
                    return "Role modifié avec succès!";
                }).orElseThrow(() -> new RuntimeException("Role n'existe pas"));
    }


    public String supprimerRoleType(Long id) {
        getCurrentAdmin();
        Optional<Role> roleTypeOptional = roleRepository.findById(id);

        if (roleTypeOptional.isPresent()) {
            roleRepository.deleteById(id);
            return "Role supprimé avec succès!";
        } else {
            return "Aucun role trouvé avec l'id fourni.";
        }
    }
    public Admin createAdmin(Admin admin) {
        getCurrentAdmin();
        return adminRepository.save(admin);
    }

    public Admin getAdminById(Long id) {
        getCurrentAdmin();
        return adminRepository.findById(id).orElse(null);
    }

    public List<Admin> getAllAdmins() {
        getCurrentAdmin();
        return adminRepository.findAll();
    }

    public void deleteAdmin(Long id) {
        getCurrentAdmin();
        adminRepository.deleteById(id);
    }
    public void addProfessionnelToAdmin(Professionnel professionnel) {
        Admin admin = getCurrentAdmin(); // Get the currently authenticated admin
        professionnel.setAdmin(admin);

        // Assume 'professionnel' contains the role information
        if (professionnel.getRole() != null) {
            // Validate role existence
            Role role = roleRepository.findById(professionnel.getRole().getId())
                    .orElseThrow(() -> new RuntimeException("Role non trouvé."));
            professionnel.setRole(role); // Set the role for the professionnel
            professionnel.setPassword(passwordEncoder.encode(professionnel.getPassword()));
        } else {
            throw new RuntimeException("Aucun rôle spécifié.");
        }

        professionnelRepositoty.save(professionnel);
    }



    @PostConstruct
    public void initAdmin() {
        try {
            Role adminRole = roleRepository.findByNom("ADMIN")
                    .orElseGet(() -> roleRepository.save(new Role("ADMIN")));

            List<User> admins = userRespository.findByRole(adminRole);

            if (admins.isEmpty()) {
                Admin admin = new Admin();
                admin.setEmail("admin@gamil.com");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setRole(adminRole);

                User savedAdmin = userRespository.save(admin);

                System.out.println("Admin créé avec succès. ID: " + savedAdmin.getId());
            } else {
                System.out.println("Un administrateur existe déjà.");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation de l'admin : " + e.getMessage());
        }
    }
}
