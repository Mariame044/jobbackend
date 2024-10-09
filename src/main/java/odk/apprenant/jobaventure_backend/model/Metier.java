package odk.apprenant.jobaventure_backend.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
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


    //@OneToMany(mappedBy = "metier")
    //@JsonManagedReference // Pour éviter les références circulaires
    //private List<Video> video = new ArrayList<>();

    //@OneToMany(mappedBy = "metier")
   // @JsonManagedReference
    //private List<Quiz> quiz = new ArrayList<>();



    //@OneToMany(mappedBy = "metier")
    //@JsonManagedReference
    //private List<Interview> interview = new ArrayList<>();

    @ManyToOne

    private Categorie categorie;

    @ManyToOne(fetch = FetchType.EAGER) // Récupération immédiate de l'Admin
    @JoinColumn(name = "admin_id")
    @JsonBackReference
    private Admin admin;

}