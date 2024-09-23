package odk.apprenant.jobaventure_backend.model;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Metier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String description;
    private String imageUrl; // or use byte[] for storing image data directly


    @OneToMany(mappedBy = "metier")
    private List<Video> video;

    @OneToMany(mappedBy = "metier")
    private List<Quiz> quiz;

    @OneToMany(mappedBy = "metier")
    private List<Jeuderole> jeuderole;

    @OneToMany(mappedBy = "metier")
    private List<Interview> interview;

    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin; // Interview associée à un professionnel
}