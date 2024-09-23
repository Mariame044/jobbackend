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




    // Ajouter un nouveau jeu de rôle
    @PostMapping("/ajouter")
    public ResponseEntity<Jeuderole> ajouterJeuDeRole(MultipartHttpServletRequest request) {
        try {
            // Extraire le fichier d'image
            MultipartFile image = request.getFile("image");
            if (image == null || image.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // Extraire les autres paramètres
            String nom = request.getParameter("nom");
            String description = request.getParameter("description");
            String metierIdStr = request.getParameter("metierId");

            // Conversion de l'ID de métier et récupération du métier
            Long metierId = Long.parseLong(metierIdStr);
            Metier metier = metierService.getMetier(metierId); // Récupérer le métier

            // Créer une nouvelle instance de Jeuderole
            Jeuderole nouveauJeu = new Jeuderole();
            nouveauJeu.setNom(nom);
            nouveauJeu.setDescription(description);
            nouveauJeu.setMetier(metier); // Lier le métier au jeu

            // Extraire les questions
            List<Question> questions = new ArrayList<>();
            int i = 0;
            while (request.getParameter("questions[" + i + "][texte]") != null) {
                Question question = new Question();
                question.setTexte(request.getParameter("questions[" + i + "][texte]"));
                question.setChoix(Arrays.asList(request.getParameter("questions[" + i + "][choix]").split(",")));
                question.setReponseCorrecte(request.getParameter("questions[" + i + "][reponseCorrecte]"));
                questions.add(question);
                i++;
            }
            nouveauJeu.setQuestions(questions); // Lier les questions au jeu

            // Ajouter le jeu de rôle via le service
            Jeuderole jeuAjoute = jeuderoleService.ajouterJeuDeRole(nouveauJeu, image);

            return new ResponseEntity<>(jeuAjoute, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    // Modifier un jeu de rôle existant
    @PutMapping("/modifier/{id}")
    public ResponseEntity<Jeuderole> modifierJeuDeRole(@PathVariable Long id,
                                                       @RequestParam("jeuderole") Jeuderole jeuderole) {
        try {
            Jeuderole jeuModifie = jeuderoleService.modifierJeuDeRole(id, jeuderole);
            return new ResponseEntity<>(jeuModifie, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }





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
