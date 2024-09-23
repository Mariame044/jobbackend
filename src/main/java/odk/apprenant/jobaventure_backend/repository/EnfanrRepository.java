package odk.apprenant.jobaventure_backend.repository;

import odk.apprenant.jobaventure_backend.model.Enfant;
import odk.apprenant.jobaventure_backend.model.Parent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnfanrRepository extends JpaRepository<Enfant, Long> {
}
