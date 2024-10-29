package odk.apprenant.jobaventure_backend.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;


import java.util.List;


@Entity
@Data
@Table(name = "jeu")
public class Jeuderole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String description;


    //@OneToMany(mappedBy = "jeuderole", cascade = CascadeType.ALL, orphanRemoval = true)
    //@JsonManagedReference
    //private List<Question> questions;


    private String imageUrl; // URL de l'image associée
    // URL ou chemin vers le fichier audio associé
    private String audioUrl;  // Nouveau champ pour l'audio

    @ManyToOne

    @JoinColumn(name = "metier_id")

    private Metier metier;// Interview associée à un professionnel

    @ManyToOne
    @JoinColumn(name = "admin_id")
    @JsonBackReference
    private Admin admin; // Interview associé

    @ManyToOne
    @JoinColumn(name = "trancheage_id")
    private Trancheage trancheage;  // Tranche d'âge associée à l'interview

}
