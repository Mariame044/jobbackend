package odk.apprenant.jobaventure_backend.service;



import odk.apprenant.jobaventure_backend.dtos.LoginUserDto;
import odk.apprenant.jobaventure_backend.dtos.RegisterUserDto;
import odk.apprenant.jobaventure_backend.dtos.UpdateUserDto;
import odk.apprenant.jobaventure_backend.model.Enfant;
import odk.apprenant.jobaventure_backend.model.Parent;
import odk.apprenant.jobaventure_backend.model.Role;
import odk.apprenant.jobaventure_backend.model.User;
import odk.apprenant.jobaventure_backend.repository.EnfanrRepository;
import odk.apprenant.jobaventure_backend.repository.ParentRepository;
import odk.apprenant.jobaventure_backend.repository.RoleRepository;
import odk.apprenant.jobaventure_backend.repository.UserRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class AuthenticationService {
    private final UserRespository userRespository;

    private final PasswordEncoder passwordEncoder;

    private final FileStorageService fileStorageService;

    private final AuthenticationManager authenticationManager;

    private final RoleRepository roleRepository;


    private ParentRepository parentRepository;
    @Autowired
    private EnfanrRepository enfanrRepository;


    public AuthenticationService(
            UserRespository userRespository,
            RoleRepository roleRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            FileStorageService fileStorageService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRespository = userRespository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileStorageService = fileStorageService;
    }




    public User authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRespository.findByEmail(input.getEmail())
                .orElseThrow();
    }

    public User signup(RegisterUserDto input) {
        // Vérifier si le rôle existe dans la base de données
        Optional<Role> roleOptional = roleRepository.findByNom(input.getRole());

        if (roleOptional.isEmpty()) {
            throw new RuntimeException("Le rôle spécifié n'existe pas");
        }

        Role role = roleOptional.get();
        User user;

        // Autoriser uniquement les Enfants et Parents à s'inscrire eux-mêmes
        if ("Enfant".equalsIgnoreCase(input.getRole())) {
            Enfant enfant = new Enfant(); // Création d'une instance d'Enfant
            enfant.setEmail(input.getEmail());
            enfant.setNom(input.getNom());
            enfant.setAge(input.getAge()); // Assurez-vous que l'âge est passé dans RegisterUserDto
            enfant.setPassword(passwordEncoder.encode(input.getPassword()));
            enfant.setRole(role); // Associer le rôle à l'enfant
            return enfanrRepository.save(enfant); // Sauvegarder l'enfant dans la base de données

        } else if ("Parent".equalsIgnoreCase(input.getRole())) {
            Parent parent = new Parent(); // Création d'une instance de Parent
            parent.setNom(input.getNom());
            parent.setProfession(input.getProfession()); // Assurez-vous que la profession est passée dans RegisterUserDto
            parent.setEmail(input.getEmail());
            parent.setPassword(passwordEncoder.encode(input.getPassword()));
            parent.setRole(role); // Associer le rôle au parent
            return parentRepository.save(parent); // Sauvegarder le parent dans la base de données
        } else {
            throw new RuntimeException("Rôle non géré : " + input.getRole());
        }
    }
    // Récupérer l'utilisateur connecté
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Récupère le nom d'utilisateur
        return userRespository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    public UpdateUserDto updateUserProfile(Long userId, String fullName, String password, String confirmPassword, MultipartFile image) throws IOException {
        // Rechercher l'utilisateur dans la base de données
        User user = userRespository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier si le mot de passe est fourni et s'il est valide
        if (password != null && !password.isEmpty()) {
            // Vérifier la confirmation du mot de passe
            if (!password.equals(confirmPassword)) {
                throw new RuntimeException("Les mots de passe ne correspondent pas");
            }

            // Encodage du mot de passe
            user.setPassword(passwordEncoder.encode(password));
        }

        // Mettre à jour l'image de profil si fournie
        if (image != null && !image.isEmpty()) {
            String imagePath = fileStorageService.sauvegarderImage(image);
            user.setImageUrl(imagePath); // Supposons que l'utilisateur ait un champ "image" pour stocker le chemin de l'image
        }

        // Mettre à jour le nom complet si fourni
        if (fullName != null && !fullName.isEmpty()) {
            user.setNom(fullName);
        }

        // Sauvegarder les changements dans la base de données
        User updatedUser = userRespository.save(user); // Sauvegarder l'utilisateur

        // Convertir l'utilisateur mis à jour en UpdateUserDto
        return new UpdateUserDto(updatedUser.getId(), updatedUser.getNom(), updatedUser.getImageUrl(), password); // Passer le mot de passe
    }


    // Méthode pour récupérer la liste des utilisateurs
    public List<User> getAllUsers() {
        return userRespository.findAll();
    }
}