package odk.apprenant.jobaventure_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String date;
    private String questions;

    @ManyToMany(mappedBy = "interview")
    private List<Enfant> enfant = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "professionnel_id")
    private Professionnel professionnel; // Interview associée à un professionnel


    @ManyToOne
    @JoinColumn(name = "metier_id")
    private Metier metier;// Interview associée à un professionnel

}

