package odk.apprenant.jobaventure_backend.controller;


import odk.apprenant.jobaventure_backend.config.JwtUtile;
import odk.apprenant.jobaventure_backend.dtos.ReqRep;
import odk.apprenant.jobaventure_backend.model.Metier;
import odk.apprenant.jobaventure_backend.model.User;
import odk.apprenant.jobaventure_backend.repository.MetierRepository;
import odk.apprenant.jobaventure_backend.repository.UserRespository;
import odk.apprenant.jobaventure_backend.service.MetierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException; // Import correct
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRespository userRespository;
    @Autowired
    private JwtUtile jwtUtile;
    @Autowired
    private MetierService metierService;

    @PostMapping("/login")
    public ReqRep login(@RequestBody ReqRep loginRequest) {
        ReqRep response = new ReqRep();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            User user = userRespository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String jwt = jwtUtile.generateToken(user);
            String refreshToken = jwtUtile.generateRefreshToken(new HashMap<>(), user);

            // Ajouter les détails de l'utilisateur dans la réponse
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRole(user.getRole());
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hrs");
            response.setNom(user.getNom());
            response.setPrenom(user.getSetFullName());
            response.setEmail(user.getEmail());
            response.setMessage("Successfully Logged In");

        } catch (Exception e) {
            e.printStackTrace();  // Afficher l'erreur dans la console
            response.setStatusCode(500);
            response.setMessage("Authentication failed: " + e.getMessage());
        }
        return response;
    }
    
}
