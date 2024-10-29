package odk.apprenant.jobaventure_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Entity
@Data
public class Parent extends User {
    private String profession;
    @OneToMany(mappedBy = "parent")
    @JsonIgnore
    private List<Enfant> enfant; // Un parent peut superviser plusieurs enfants
    // Méthode pour obtenir la progression de chaque enfant
    public Map<Long, Map<String, Object>> getProgressionDesEnfants() {
        Map<Long, Map<String, Object>> progressionEnfants = new HashMap<>();
        for (Enfant enfant : enfant) {
            progressionEnfants.put(enfant.getId(), enfant.getProgression());
        }
        return progressionEnfants; // Retourne la progression de tous les enfants
    }
}
