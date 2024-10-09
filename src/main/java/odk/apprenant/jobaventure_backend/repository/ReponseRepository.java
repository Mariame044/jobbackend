package odk.apprenant.jobaventure_backend.repository;

import odk.apprenant.jobaventure_backend.model.Reponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ReponseRepository extends JpaRepository<Reponse, Long> {


    // Méthode pour trouver une réponse par sa valeur
    Reponse findByReponsepossible(String reponsePossible);
}
