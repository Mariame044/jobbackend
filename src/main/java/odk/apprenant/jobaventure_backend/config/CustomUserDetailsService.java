package odk.apprenant.jobaventure_backend.config;

import odk.apprenant.jobaventure_backend.model.User;
import odk.apprenant.jobaventure_backend.repository.UserRespository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRespository userRespository;

    public CustomUserDetailsService(UserRespository userRespository) {
        this.userRespository = userRespository;

    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User utilisateur = userRespository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable avec l'email : " + email));

        // Retourne un UserDetails avec l'email, le mot de passe, et les rôles de l'utilisateur
        return new org.springframework.security.core.userdetails.User(
                utilisateur.getEmail(),
                utilisateur.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + utilisateur.getRole().getNom()))
        );
    }
}
