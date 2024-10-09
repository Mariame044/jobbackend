package odk.apprenant.jobaventure_backend.repository;


import odk.apprenant.jobaventure_backend.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByMetierId(Long metierId);
    // Vous pouvez ajouter des méthodes de requêtes personnalisées ici si nécessaire.
}