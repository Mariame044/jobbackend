package odk.apprenant.jobaventure_backend.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
@Data
public class UpdateUserDto {

    private Long id;            // Type Long pour l'ID
    private String nom;        // Type String pour le nom
    private String imageUrl;   // Type String pour l'URL de l'image
    private String password;    // Type String pour le mot de passe

    // Constructeur
    public UpdateUserDto(Long id, String nom, String imageUrl, String password) {
        this.id = id;
        this.nom = nom;
        this.imageUrl = imageUrl;
        this.password = password;  // Initialiser le mot de passe
    }

    // Getters et setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}