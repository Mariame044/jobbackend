package odk.apprenant.jobaventure_backend.repository;

import odk.apprenant.jobaventure_backend.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
