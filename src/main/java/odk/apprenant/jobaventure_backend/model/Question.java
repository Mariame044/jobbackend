package odk.apprenant.jobaventure_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer point;
    private String texte;
    @Enumerated(EnumType.STRING)

    private TypeQuestion typeQuestion;  // Enum pour type de question (QUIZ ou JEU_DE_ROLE)

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;  // Une question est liée à un seul quiz

    @ManyToOne // Assuming many questions can belong to one Jeuderole
    @JoinColumn(name = "jeu_de_role_id") // Foreign key column name
    private Jeuderole jeuderole; // This must match the mappedBy in Jeuderole

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<Reponse> reponses;  // Une question peut avoir plusieurs réponses
}