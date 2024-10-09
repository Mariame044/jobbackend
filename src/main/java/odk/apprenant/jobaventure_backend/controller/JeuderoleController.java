package odk.apprenant.jobaventure_backend.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import odk.apprenant.jobaventure_backend.dtos.MetierDto;
import odk.apprenant.jobaventure_backend.model.Interview;
import odk.apprenant.jobaventure_backend.model.Jeuderole;
import odk.apprenant.jobaventure_backend.model.Metier;
import odk.apprenant.jobaventure_backend.model.Question;
import odk.apprenant.jobaventure_backend.repository.JeuderoleRepository;
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
import java.util.Map;

@RestController
@RequestMapping("api/jeux")
public class JeuderoleController {
    @Autowired
    private JeuderoleService jeuderoleService;
    @Autowired
    private MetierService metierService;
    @Autowired
    private JeuderoleRepository jeuderoleRepository;
    @Autowired
    private ObjectMapper objectMapper; // Déclaration de ObjectMapper


    // Ajouter un jeu de rôle
    // Endpoint pour ajouter une nouvelle interview
    @PostMapping
    public ResponseEntity<Jeuderole> ajouterJeuDeRole(MultipartHttpServletRequest request) {
        try {
            // Extraire le fichier vidéo
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
            MetierDto metierDto = metierService.getMetier(metierId); // Récupération du métier

            // Créer une nouvelle instance de Interview
            Jeuderole nouvelleJeuderole = new Jeuderole();

            nouvelleJeuderole.setNom(nom);
            nouvelleJeuderole.setDescription(description);
            nouvelleJeuderole.setMetier(convertToEntity(metierDto)); // Convertir MetierDto en Metier

            // Ajouter l'interview via le service
            Jeuderole jeuderoleAjoutee = jeuderoleService.ajouterJeuDeRole(nouvelleJeuderole, image);

            // Définir l'URL
            String url = "http://localhost:8080/uploads/images/" + image.getOriginalFilename();
            jeuderoleAjoutee.setImageUrl(url); // Ajouter l'URL au modèle

            return new ResponseEntity<>( jeuderoleAjoutee, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NumberFormatException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    // Méthode pour convertir MetierDto en Metier
    private Metier convertToEntity(MetierDto metierDto) {
        Metier metier = new Metier();
        metier.setId(metierDto.getId());
        metier.setNom(metierDto.getNom());
        metier.setDescription(metierDto.getDescription());
        // Assurez-vous d'ajouter d'autres attributs de Metier si nécessaire
        return metier;
    }

    // Modifier un jeu de rôle
    @PutMapping("/{id}")
    public ResponseEntity<Jeuderole> modifierJeuDeRole(@PathVariable Long id, @RequestBody Jeuderole jeuderole) {
        Jeuderole jeuModifie = jeuderoleService.modifierJeuDeRole(id, jeuderole);
        return ResponseEntity.ok(jeuModifie);
    }

    // Supprimer un jeu de rôle
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerJeuDeRole(@PathVariable Long id) {
        jeuderoleService.supprimerJeuDeRole(id);
        return ResponseEntity.noContent().build();
    }

    // Obtenir les détails d'un jeu de rôle
    @GetMapping("/{id}")
    public ResponseEntity<Jeuderole> getJeuDeRoleDetails(@PathVariable Long id) {
        Jeuderole jeuDeRole = jeuderoleService.getJeuDeRoleDetails(id);
        return ResponseEntity.ok(jeuDeRole);
    }



    // Vérifier la réponse d'un enfant
    @PostMapping("/{jeuId}/verifier-reponse")
    public ResponseEntity<String> verifierReponse(@RequestParam Long enfantId,
                                                  @PathVariable Long jeuId,
                                                  @RequestParam Long questionId,
                                                  @RequestParam String reponseDonnee) {
        String resultat = jeuderoleService.verifierReponse(enfantId, jeuId, questionId, reponseDonnee);
        return ResponseEntity.ok(resultat);
    }

    // Calculer le score d'un enfant
    @PostMapping("/{jeuId}/calculer-score")
    public ResponseEntity<Integer> calculerScore(@RequestParam Long enfantId,
                                                 @PathVariable Long jeuId,
                                                 @RequestBody Map<Long, String> reponsesDonnees) {
        int score = jeuderoleService.calculerScore(enfantId, jeuId, reponsesDonnees);
        return ResponseEntity.ok(score);
    }
    @GetMapping
    public ResponseEntity<List<Jeuderole>> getAllJeuDeRole() {
        List<Jeuderole> jeuxDeRole = jeuderoleRepository.findAll();
        return new ResponseEntity<>(jeuxDeRole, HttpStatus.OK);
    }
}