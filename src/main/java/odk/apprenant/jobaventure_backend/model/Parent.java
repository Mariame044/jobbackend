package odk.apprenant.jobaventure_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;


@Entity
@Data
public class Parent extends User {
    private String profession;
    @OneToMany(mappedBy = "parent")
    private List<Enfant> enfant; // Un parent peut superviser plusieurs enfants
}
