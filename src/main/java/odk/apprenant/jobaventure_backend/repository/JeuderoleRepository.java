package odk.apprenant.jobaventure_backend.repository;

import odk.apprenant.jobaventure_backend.model.Jeuderole;
import odk.apprenant.jobaventure_backend.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JeuderoleRepository extends JpaRepository<Jeuderole, Long> {
    List<Jeuderole> findByMetierId(Long metierId);
}
