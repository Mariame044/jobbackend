package odk.apprenant.jobaventure_backend.repository;

import odk.apprenant.jobaventure_backend.model.Admin;
import odk.apprenant.jobaventure_backend.model.Professionnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProfessionnelRepositoty extends JpaRepository<Professionnel, Long> {
    Optional<Professionnel> findByEmail(String email);
}
