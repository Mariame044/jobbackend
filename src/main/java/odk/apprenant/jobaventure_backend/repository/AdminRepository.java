package odk.apprenant.jobaventure_backend.repository;

import odk.apprenant.jobaventure_backend.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);
}