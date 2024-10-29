package odk.apprenant.jobaventure_backend.repository;

import odk.apprenant.jobaventure_backend.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByJeuderoleId(Long jeuderoleId);
    List<Question> findByquizId(Long quizId);
}
