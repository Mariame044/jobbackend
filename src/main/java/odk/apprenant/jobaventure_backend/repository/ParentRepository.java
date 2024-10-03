package odk.apprenant.jobaventure_backend.repository;

import odk.apprenant.jobaventure_backend.model.Admin;
import odk.apprenant.jobaventure_backend.model.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping
public interface ParentRepository extends JpaRepository<Parent, Long> {
    Optional<Parent> findByEmail(String email);
}

