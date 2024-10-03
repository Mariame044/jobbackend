package odk.apprenant.jobaventure_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;
    private String duree;
    private String description;
    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin; // L'admin qui a ajouté la vidéo

    @ManyToMany(mappedBy = "video")
    private List<Enfant> enfant = new ArrayList<>();
    // Nouveau champ pour le nombre de vues
    private int nombreDeVues = 0; // Valeur par défaut à 0

    @ManyToOne
    @JoinColumn(name = "metier_id")
    private Metier metier;
}

