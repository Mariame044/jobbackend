package odk.apprenant.jobaventure_backend.repository;

import odk.apprenant.jobaventure_backend.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
}
