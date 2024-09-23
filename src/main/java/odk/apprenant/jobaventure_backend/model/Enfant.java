package odk.apprenant.jobaventure_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Enfant extends User {
    private String age;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent; // Relation avec le parent (plusieurs enfants pour un parent)

    @ManyToMany
    @JoinTable(
            name = "enfant_badge",
            joinColumns = @JoinColumn(name = "enfant_id"),
            inverseJoinColumns = @JoinColumn(name = "badge_id")
    )
    private List<Badge> badge = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "enfant_Quiz",
            joinColumns = @JoinColumn(name = "enfant_id"),
            inverseJoinColumns = @JoinColumn(name = "quiz_id")
    )
    private List<Quiz> quiz = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "enfant_video",
            joinColumns = @JoinColumn(name = "enfant_id"),
            inverseJoinColumns = @JoinColumn(name = "video_id")
    )
    private List<Video> video = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "enfant_jeu",
            joinColumns = @JoinColumn(name = "enfant_id"),
            inverseJoinColumns = @JoinColumn(name = "jeu_id")
    )
    private List<Jeuderole> jeuderole = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "enfant_interview",
            joinColumns = @JoinColumn(name = "enfant_id"),
            inverseJoinColumns = @JoinColumn(name = "interview_id")
    )
    private List<Interview> interview = new ArrayList<>();



}
