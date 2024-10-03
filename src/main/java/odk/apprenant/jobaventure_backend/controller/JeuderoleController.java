package odk.apprenant.jobaventure_backend.controller;


import odk.apprenant.jobaventure_backend.model.Jeuderole;
import odk.apprenant.jobaventure_backend.model.Metier;
import odk.apprenant.jobaventure_backend.model.Question;
import odk.apprenant.jobaventure_backend.service.JeuderoleService;
import odk.apprenant.jobaventure_backend.service.MetierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/jeux")
public class JeuderoleController {
    @Autowired
    private JeuderoleService jeuderoleService;
    @Autowired
    private MetierService metierService;






    // Supprimer un jeu de rôle
    @DeleteMapping("/supprimer/{id}")
    public ResponseEntity<Void> supprimerJeuDeRole(@PathVariable Long id) {
        try {
            jeuderoleService.supprimerJeuDeRole(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Jouer au jeu et récupérer les questions
    @GetMapping("/jouer/{id}")
    public ResponseEntity<List<Question>> jouerJeu(@PathVariable Long id) {
        try {
            List<Question> questions = jeuderoleService.jouer(id);
            return new ResponseEntity<>(questions, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
