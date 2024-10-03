package odk.apprenant.jobaventure_backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;


@Entity
@Data
public class Professionnel extends User{
    private String secteur;
    private String entreprise;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    @JsonBackReference // Évite la boucle lors de la sérialisation
    private Admin admin; // Interview associée à un professionnel

   
}
