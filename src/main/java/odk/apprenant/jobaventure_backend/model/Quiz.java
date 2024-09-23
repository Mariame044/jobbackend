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

    private String question;
    private String reponse;

    @ManyToOne
    @JoinColumn(name = "metier_id")
    private Metier metier;

    @ManyToMany(mappedBy = "quiz")
    private List<Enfant> enfant = new ArrayList<>();
}