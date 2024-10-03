package odk.apprenant.jobaventure_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "question1")
public class Question1 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contenu;  // La question elle-même
    private String choix1;
    private String choix2;
    private String choix3;
    private String choix4;
    private String reponseCorrecte;

    // Relation ManyToOne avec Quiz
    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;
}
