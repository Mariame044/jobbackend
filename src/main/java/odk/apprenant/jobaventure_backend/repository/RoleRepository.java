package odk.apprenant.jobaventure_backend.repository;

import odk.apprenant.jobaventure_backend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByNom(String nom);
}
