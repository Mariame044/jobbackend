package odk.apprenant.jobaventure_backend.service;


import lombok.AllArgsConstructor;
import odk.apprenant.jobaventure_backend.dtos.RegisterUserDto;
import odk.apprenant.jobaventure_backend.model.Enfant;
import odk.apprenant.jobaventure_backend.model.Parent;
import odk.apprenant.jobaventure_backend.model.Role;
import odk.apprenant.jobaventure_backend.model.User;
import odk.apprenant.jobaventure_backend.repository.EnfanrRepository;
import odk.apprenant.jobaventure_backend.repository.ParentRepository;
import odk.apprenant.jobaventure_backend.repository.RoleRepository;
import odk.apprenant.jobaventure_backend.repository.UserRespository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UseService implements UserDetailsService {
@Autowired
    private UserRespository userRespository;



    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRespository.findByEmail(email).orElseThrow();
    }
}




