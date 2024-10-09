package odk.apprenant.jobaventure_backend.dtos;

import lombok.Data;
import odk.apprenant.jobaventure_backend.model.Role;

@Data
public class UserDto {
    private long id;
    private Role role;
    private String nom;
    private String email; // Ajoutez d'autres attributs si nécessaire

    // Ajoutez d'autres attributs selon vos besoins
// Constructeur mis à jour pour accepter Role
    public UserDto(Long id, String nom, String email, Role role) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.role = role;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
