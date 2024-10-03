package odk.apprenant.jobaventure_backend.service;



import odk.apprenant.jobaventure_backend.dtos.LoginUserDto;
import odk.apprenant.jobaventure_backend.dtos.RegisterUserDto;
import odk.apprenant.jobaventure_backend.model.Enfant;
import odk.apprenant.jobaventure_backend.model.Parent;
import odk.apprenant.jobaventure_backend.model.Role;
import odk.apprenant.jobaventure_backend.model.User;
import odk.apprenant.jobaventure_backend.repository.RoleRepository;
import odk.apprenant.jobaventure_backend.repository.UserRespository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    // Méthode pour mettre à jour les informations de l'utilisateur
    public User updateUserProfile(Long userId, String fullName, String password, MultipartFile image) throws IOException {
        // Rechercher l'utilisateur dans la base de données
        User user = userRespository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));


        // Mettre à jour le mot de passe si fourni
        if (password != null && !password.isEmpty()) {
            user.setPassword(passwordEncoder.encode(password));
        }

        // Mettre à jour l'image de profil si fournie
        if (image != null && !image.isEmpty()) {
            String imagePath = fileStorageService.sauvegarderImage(image);
            user.setImageUrl(imagePath); // Supposons que l'utilisateur ait un champ "image" pour stocker le chemin de l'image
        }

        // Sauvegarder les changements dans la base de données
        return userRespository.save(user);
    }
    // Méthode pour récupérer la liste des utilisateurs
    public List<User> getAllUsers() {
        return userRespository.findAll();
    }
}