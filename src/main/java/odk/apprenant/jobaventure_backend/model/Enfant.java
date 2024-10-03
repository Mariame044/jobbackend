package odk.apprenant.jobaventure_backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class Enfant extends User {
    private String age;
    // Tentatives de réponses
    private int tentativesRestantes = 3;  // Initialement 3 tentatives
    private Date derniereTentative;        // Dernière tentative
    private boolean enAttente;              // Indique si l'enfant est en attente
    @ManyToOne
    @JoinColumn(name = "parent_id")
    @JsonManagedReference
    private Parent parent; // Relation avec le parent (plusieurs enfants pour un parent)

    @ManyToMany
    @JoinTable(
            name = "enfant_badge",
            joinColumns = @JoinColumn(name = "enfant_id"),
            inverseJoinColumns = @JoinColumn(name = "badge_id")
    )
    @JsonManagedReference
    private List<Badge> badge = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "enfant_Quiz",
            joinColumns = @JoinColumn(name = "enfant_id"),
            inverseJoinColumns = @JoinColumn(name = "quiz_id")
    )
    @JsonManagedReference
    private List<Quiz> quiz = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "enfant_video",
            joinColumns = @JoinColumn(name = "enfant_id"),
            inverseJoinColumns = @JoinColumn(name = "video_id")
    )
    @JsonManagedReference
    private List<Video> video = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "enfant_jeu",
            joinColumns = @JoinColumn(name = "enfant_id"),
            inverseJoinColumns = @JoinColumn(name = "jeu_id")
    )
    @JsonManagedReference
    private List<Jeuderole> jeuderole = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "enfant_interview",
            joinColumns = @JoinColumn(name = "enfant_id"),
            inverseJoinColumns = @JoinColumn(name = "interview_id")
    )
    @JsonManagedReference
    private List<Interview> interview = new ArrayList<>();



}
