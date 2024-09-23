package odk.apprenant.jobaventure_backend.model;

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
    private Admin admin; // Interview associée à un professionnel

    @OneToMany(mappedBy = "professionnel", cascade = CascadeType.ALL)
    private List<Interview> interview; // Un professionnel peut avoir plusieurs interviews


}
