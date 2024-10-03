package odk.apprenant.jobaventure_backend.repository;

import odk.apprenant.jobaventure_backend.model.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
}
