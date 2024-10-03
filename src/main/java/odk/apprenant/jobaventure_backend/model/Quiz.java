package odk.apprenant.jobaventure_backend.model;

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
    // Relation OneToMany avec Question (si un quiz a plusieurs questions)
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    private List<Question> question = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "metier_id")
    private Metier metier;

    @ManyToMany(mappedBy = "quiz")
    private List<Enfant> enfant = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    private List<Question> questions;  // Un quiz contient plusieurs questions

    @ManyToOne
    @JoinColumn(name = "badge_id")
    private Badge badge;  // Un quiz est lié à un seul badge

}