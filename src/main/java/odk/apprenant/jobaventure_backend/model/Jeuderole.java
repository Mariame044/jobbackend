package odk.apprenant.jobaventure_backend.model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "jeu")
public class Jeuderole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String description;



    @OneToMany(mappedBy = "jeuderole") // Assurez-vous que 'jeuDeRole' existe dans Question
    private List<Question> question;

    private String imageUrl; // URL de l'image associée

    @ManyToOne
    @JoinColumn(name = "metier_id")
    @JsonManagedReference // Gérer la sérialisation des jeux de rôle
    private Metier metier;// Interview associée à un professionnel

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin; // Interview associé


    @ManyToMany(mappedBy = "jeuderole")
    private List<Enfant> enfant = new ArrayList<>();
}
