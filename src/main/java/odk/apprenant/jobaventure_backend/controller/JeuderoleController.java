package odk.apprenant.jobaventure_backend.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import odk.apprenant.jobaventure_backend.dtos.MetierDto;
import odk.apprenant.jobaventure_backend.model.*;
import odk.apprenant.jobaventure_backend.repository.JeuderoleRepository;
import odk.apprenant.jobaventure_backend.repository.TrancheageRepository;
import odk.apprenant.jobaventure_backend.service.JeuderoleService;
import odk.apprenant.jobaventure_backend.service.MetierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.*;

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
    @Autowired
    private TrancheageRepository trancheageRepository;


    // Ajouter un jeu de rôle
    // Endpoint pour ajouter une nouvelle interview
    @PostMapping
    public ResponseEntity<Jeuderole> ajouterJeuDeRole(MultipartHttpServletRequest request) {
            try {
                MultipartFile image = request.getFile("image");
                MultipartFile audio = request.getFile("audio");

                if (image == null || image.isEmpty() || audio == null || audio.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            // Extraire les autres paramètres

            String nom = request.getParameter("nom");
            String description = request.getParameter("description");
            String metierIdStr = request.getParameter("metierId");
            String trancheageIdStr = request.getParameter("trancheageId");

            // Conversion de l'ID de métier et récupération du métier
            Long metierId = Long.parseLong(metierIdStr);
            MetierDto metierDto = metierService.getMetier(metierId); // Récupération du métier
            Long trancheageId = Long.parseLong(trancheageIdStr);
            Trancheage trancheage = trancheageRepository.findById(trancheageId)
                    .orElseThrow(() -> new RuntimeException("Tranche d'âge non trouvée avec l'ID : " + trancheageId));


            // Créer une nouvelle instance de Interview
            Jeuderole nouvelleJeuderole = new Jeuderole();

            nouvelleJeuderole.setNom(nom);
            nouvelleJeuderole.setDescription(description);
            nouvelleJeuderole.setMetier(convertToEntity(metierDto)); // Convertir MetierDto en Metier
            nouvelleJeuderole.setTrancheage(trancheage); // Lier la tranche d'âge à la vidéo

            // Ajouter l'interview via le service
            Jeuderole jeuderoleAjoutee = jeuderoleService.ajouterJeuDeRole(nouvelleJeuderole, image, audio);

            // Définir l'URL
            String url = "http://localhost:8080/uploads/images/" + image.getOriginalFilename();
            jeuderoleAjoutee.setImageUrl(url); // Ajouter l'URL au modèle

                String audioUrl = "http://localhost:8080/uploads/audios/" + audio.getOriginalFilename();
                jeuderoleAjoutee.setAudioUrl(audioUrl);  // Set the audio URL


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

    // Méthode pour récupérer les vidéos d'un enfant
    @GetMapping("/pour-enfant")
    public ResponseEntity<List<Jeuderole>> obtenirVideosPourEnfantConnecte() {
        try {
            List<Jeuderole> jeuderolesPourEnfant = jeuderoleService.trouverVideosPourEnfantParAge(); // Méthode ajustée
            return ResponseEntity.ok(jeuderolesPourEnfant);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/{jeuId}/jouer") // Annotation pour gérer l'URL /api/jeux/{jeuId}/jouer
    public List<Question> jouer(@PathVariable Long jeuId) {
        return jeuderoleService.jouer(jeuId); // Appel de la méthode dans le service
    }
    // Vérifier la réponse d'une question
    @PostMapping("/{jeuId}/questions/{questionId}/verifier")
    public ResponseEntity<Map<String, String>> verifierReponse(
            @PathVariable Long jeuId,
            @PathVariable Long questionId,
            @RequestBody Map<String, String> body) {
        String reponseDonnee = body.get("reponseDonnee");
        String resultat = jeuderoleService.verifierReponse(jeuId, questionId, reponseDonnee);

        // Renvoyer la réponse au format JSON
        Map<String, String> response = new HashMap<>();
        response.put("message", resultat); // Le message que vous souhaitez renvoyer
        return ResponseEntity.ok(response);
    }



    // Calculer le score basé sur les réponses données
    @PostMapping("/{jeuId}/calculerScore")
    public ResponseEntity<Integer> calculerScore(
            @PathVariable Long jeuId,
            @RequestBody Map<Long, String> reponsesDonnees) {
        int score = jeuderoleService.calculerScore(jeuId, reponsesDonnees);
        return ResponseEntity.ok(score);
    }


    @GetMapping
    public ResponseEntity<List<Jeuderole>> getAllJeuDeRole() {
        List<Jeuderole> jeuxDeRole = jeuderoleRepository.findAll();
        return new ResponseEntity<>(jeuxDeRole, HttpStatus.OK);
    }

    @GetMapping("/pour-enfant/metier/{metierId}")
    public ResponseEntity<List<Jeuderole>> obtenirVideosParMetierEtAge(@PathVariable Long metierId) {
        try {
            List<Jeuderole> jeuderoles = jeuderoleService.trouverJeuderoleParMetierEtAge(metierId); // Appel à la nouvelle méthode
            return ResponseEntity.ok(jeuderoles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}