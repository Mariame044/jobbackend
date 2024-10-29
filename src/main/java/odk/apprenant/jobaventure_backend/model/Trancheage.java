package odk.apprenant.jobaventure_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Trancheage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int ageMin;
    private int ageMax;

    private String description; // Par exemple : "6-8 ans", "9-12 ans", etc.

    // Ajoute un nom ou une description pour faciliter la gestion des tranches
    public boolean isAgeInTranche(int age) {
        return age >= ageMin && age <= ageMax;
    }
}
