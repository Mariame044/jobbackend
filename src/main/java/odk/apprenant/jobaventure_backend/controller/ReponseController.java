package odk.apprenant.jobaventure_backend.controller;


import odk.apprenant.jobaventure_backend.model.Reponse;
import odk.apprenant.jobaventure_backend.service.ReponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/reponse")
public class ReponseController {

    @Autowired
    private ReponseService reponseService;

    // Obtenir toutes les réponses
    @GetMapping
    public List<Reponse> getAllReponses() {
        return reponseService.getAllReponses();
    }

    // Obtenir une réponse par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Reponse> getReponseById(@PathVariable int id) {
        Optional<Reponse> reponse = reponseService.getReponseById(id);
        return reponse.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Créer une nouvelle réponse avec des questions associées

    @PostMapping
    public ResponseEntity<Reponse> createReponse(@RequestBody Reponse reponse) {
        Reponse newReponse = reponseService.createReponse(reponse);
        return ResponseEntity.ok(newReponse);
    }

    // Mettre à jour une réponse par son ID
    @PutMapping("/{id}")
    public ResponseEntity<Reponse> updateReponse(@PathVariable int id, @RequestBody Reponse reponseDetails, @RequestParam List<Long> questionIds) {
        try {
            Reponse updatedReponse = reponseService.updateReponse(id, reponseDetails, questionIds);
            return ResponseEntity.ok(updatedReponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Supprimer une réponse par son ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReponse(@PathVariable int id) {
        try {
            reponseService.deleteReponse(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}