package odk.apprenant.jobaventure_backend.repository;

import odk.apprenant.jobaventure_backend.model.Admin;
import odk.apprenant.jobaventure_backend.model.Enfant;
import odk.apprenant.jobaventure_backend.model.Parent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnfanrRepository extends JpaRepository<Enfant, Long> {
    Optional<Enfant> findByEmail(String email);
    List<Enfant> findByParent(Parent parent);

}
