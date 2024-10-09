package odk.apprenant.jobaventure_backend.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class RegisterUserDto {
    private String email;
    private String password;
    private String nom;
    private String age;
    private String profession;
    private MultipartFile image; // Ajouter ce champ pour l'image
    private String role;
}
