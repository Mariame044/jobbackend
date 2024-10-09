package odk.apprenant.jobaventure_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private String date;
    private String url;
    private String duree;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin; // L'admin qui a ajouté la vidéo
    private int nombreDeVues = 0; // Valeur par défaut à 0
    @ManyToOne
    @JoinColumn(name = "metier_id")

    private Metier metier;// Interview associée à un professionnel

}

