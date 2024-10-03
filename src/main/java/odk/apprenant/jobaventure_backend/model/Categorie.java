package odk.apprenant.jobaventure_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "categorie")
public class Categorie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nom;

    @OneToMany
    private List<Metier> metier; // Une catégorie contient plusieurs métiers

    @ManyToOne

    private Admin admin; // Interview associée à un professionnel

}
