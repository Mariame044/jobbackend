package odk.apprenant.jobaventure_backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    private String description;
    private Integer score;
    private Integer resultat;
    private Boolean badget;



    @ManyToOne
    @JoinColumn(name = "metier_id")

    private Metier metier;// Interview associée à un professionnel

    @ManyToOne
    @JoinColumn(name = "trancheage_id")
    private Trancheage trancheage;  // Tranche d'âge associée à l'interview

    @ManyToOne
    @JoinColumn(name = "admin_id")
    @JsonBackReference
    private Admin admin; // Interview associé


    @ManyToOne
    @JoinColumn(name = "badge_id")
    private Badge badge;  // Un quiz est lié à un seul badge

    // Si vous avez une relation avec Enfant, décommenter la section suivante
    //@ManyToMany(mappedBy = "quiz")
    //private List<Enfant> enfants = new ArrayList<>();
}
