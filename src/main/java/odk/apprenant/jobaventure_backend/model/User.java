package odk.apprenant.jobaventure_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data

public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    @Column(unique = true)
    private String email;
    private String password;
    private String imageUrl;
    @ManyToOne
    @JoinColumn(name = "id_role")
    private Role role;


    // Constructeur, getters et setters
}
