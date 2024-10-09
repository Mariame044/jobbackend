package odk.apprenant.jobaventure_backend.controller;


import odk.apprenant.jobaventure_backend.dtos.UpdateUserDto;
import odk.apprenant.jobaventure_backend.model.User;
import odk.apprenant.jobaventure_backend.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("api/modifier")
public class UpdateController {

    @Autowired
    private AuthenticationService authenticationService;

    // Endpoint pour récupérer l'utilisateur connecté
    @GetMapping
    public ResponseEntity<User> getCurrentUser() {
        User user = authenticationService.getCurrentUser();
        return ResponseEntity.ok(user);
    }
    @PutMapping("/current")
    public ResponseEntity<UpdateUserDto> updateCurrentUserProfile(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String confirmPassword, // Ajouter ce paramètre
            @RequestParam(required = false) MultipartFile image) {
        try {
            User currentUser = authenticationService.getCurrentUser();
            UpdateUserDto updatedUser = authenticationService.updateUserProfile(currentUser.getId(), fullName, password, confirmPassword, image);
            return ResponseEntity.ok(updatedUser);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Erreur serveur
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Problème de validation
        }
    }

}
