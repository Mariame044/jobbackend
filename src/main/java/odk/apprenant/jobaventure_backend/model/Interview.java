package odk.apprenant.jobaventure_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titre;
    private String description;
    @Column(name = "date") // Vous pouvez spécifier le nom de la colonne
    private LocalDateTime date; // Changez Datetime à LocalDateTime
    private String url;
    private String duree;

    @ManyToOne
    @JoinColumn(name = "trancheage_id")
    private Trancheage trancheage;  // Tranche d'âge associée à l'interview
    @ManyToMany
    @JsonManagedReference// Assurez-vous que cela correspond au nom de la propriété dans Enfant
    private List<Enfant> enfant;
    @ManyToOne
    @JsonIgnore // Ignore l'admin pour alléger la réponse JSON
    @JoinColumn(name = "admin_id")
    private Admin admin; // L'admin qui a ajouté la vidéo
    private int nombreDeVues = 0; // Valeur par défaut à 0
    @ManyToOne
    @JoinColumn(name = "metier_id")

    private Metier metier;// Interview associée à un professionnel

}

