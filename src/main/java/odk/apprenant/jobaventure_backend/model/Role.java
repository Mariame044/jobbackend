package odk.apprenant.jobaventure_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "role")
@Data

public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    // Constructeur par défaut
    public Role() {}

    // Constructeur avec nom
    public Role(String nom) {
        this.nom = nom;
    }


}