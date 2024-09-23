package odk.apprenant.jobaventure_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Data
@Table(name = "categorie")
public class Categorie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nom;

    @OneToMany(mappedBy = "categorie", cascade = CascadeType.ALL)
    private Set<Metier> metier; // Une catégorie contient plusieurs métiers

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin; // Interview associée à un professionnel

}
