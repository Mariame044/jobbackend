package odk.apprenant.jobaventure_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;
    private String duree;
    private String titre;
    private String description;
    @ManyToOne
    @JsonIgnore // Ignore l'admin pour alléger la réponse JSON
    @JoinColumn(name = "admin_id")
    private Admin admin; // L'admin qui a ajouté la vidéo

    @ManyToOne
    @JoinColumn(name = "trancheage_id")
    private Trancheage trancheage;  // Tranche d'âge associée à l'interviewC


    @ManyToMany
    @JsonManagedReference// Assurez-vous que cela correspond au nom de la propriété dans Enfant
    private List<Enfant> enfant;
    private int nombreDeVues = 0; // Valeur par défaut à 0

    @ManyToOne
    @JoinColumn(name = "metier_id")
    private Metier metier;
}

