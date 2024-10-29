package odk.apprenant.jobaventure_backend.repository;

import odk.apprenant.jobaventure_backend.dtos.QuizDto;
import odk.apprenant.jobaventure_backend.model.Quiz;
import odk.apprenant.jobaventure_backend.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByMetierId(Long metierId);
}
