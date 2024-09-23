package odk.apprenant.jobaventure_backend.model;


import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "jeu")
public class Jeuderole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String description;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "jeu_id") // Nom de la colonne dans la table Question
    private List<Question> questions = new ArrayList<>();
    private String imageUrl; // URL de l'image associée

    @ManyToOne
    @JoinColumn(name = "metier_id")
    private Metier metier;// Interview associée à un professionnel

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin; // Interview associé


    @ManyToMany(mappedBy = "jeuderole")
    private List<Enfant> enfant = new ArrayList<>();
}
