package odk.apprenant.jobaventure_backend.model;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data

public class Reponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ElementCollection
    private List<String> reponsepossible;  // Liste des choix possibles pour la question
    private String correct;
    ///@OneToMany(mappedBy = "reponse")  // Relation OneToMany avec Question
    //private List<Question> question;
}
