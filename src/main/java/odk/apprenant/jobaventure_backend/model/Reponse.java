package odk.apprenant.jobaventure_backend.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data

public class Reponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String libelle;
    private Boolean correct;
    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;  // Une réponse est liée à une seule question
}
