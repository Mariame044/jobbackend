package odk.apprenant.jobaventure_backend.service;


import lombok.AllArgsConstructor;
import odk.apprenant.jobaventure_backend.model.User;
import odk.apprenant.jobaventure_backend.repository.UserRespository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UseService implements UserDetailsService {

    private UserRespository userRespository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRespository.findByEmail(email).orElseThrow();
    }






}