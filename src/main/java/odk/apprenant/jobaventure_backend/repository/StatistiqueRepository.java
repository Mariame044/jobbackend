package odk.apprenant.jobaventure_backend.repository;

import odk.apprenant.jobaventure_backend.model.Statistique;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface StatistiqueRepository extends JpaRepository<Statistique, Long> {
    Optional<Statistique> findByMetierId(Long metierId);
    Optional<Statistique> findByVideoId(Long videoId);
    Optional<Statistique> findByQuizId(Long quizId);
}
