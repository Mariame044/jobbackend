package odk.apprenant.jobaventure_backend.controller;


import jakarta.persistence.EntityNotFoundException;
import odk.apprenant.jobaventure_backend.model.Enfant;
import odk.apprenant.jobaventure_backend.model.Parent;
import odk.apprenant.jobaventure_backend.service.ParentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/parents")
public class ParentController {

    @Autowired
    private ParentService parentService;

    // Endpoint pour créer un nouveau parent


    @PostMapping("/register")
    public ResponseEntity<Parent> registerEnfant(@RequestBody Parent parent) {
        try {
            Parent savedparent = parentService.registerParent(parent);
            return ResponseEntity.ok(savedparent);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Endpoint pour récupérer tous les parents
    @GetMapping("/tous")
    public List<Parent> obtenirTousLesParents() {
        return parentService.obtenirTousLesParents();
    }

    // Endpoint pour récupérer un parent par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Parent> obtenirParentParId(@PathVariable Long id) {
        Optional<Parent> parent = parentService.obtenirParentParId(id);
        return parent.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Endpoint pour mettre à jour un parent
    @PutMapping("/mettre-a-jour/{id}")
    public ResponseEntity<Parent> mettreAJourParent(@PathVariable Long id, @RequestBody Parent parentDetails) {
        Parent parentMisAJour = parentService.mettreAJourParent(id, parentDetails);
        return ResponseEntity.ok(parentMisAJour);
    }

    // Endpoint pour supprimer un parent
    @DeleteMapping("/supprimer/{id}")
    public ResponseEntity<Void> supprimerParent(@PathVariable Long id) {
        parentService.supprimerParent(id);
        return ResponseEntity.noContent().build();
    }
    // Endpoint pour superviser un enfant
    @PostMapping("/supervise-enfant/{enfantEmail}")
    public ResponseEntity<Enfant> superviseEnfant(@PathVariable String  enfantEmail) {
        Enfant enfantSupervise = parentService.superviseEnfant( enfantEmail);
        return ResponseEntity.ok(enfantSupervise); // Retourner l'enfant supervisé avec un statut 200
    }
    // Endpoint pour voir la progression d'un enfant supervisé
    @GetMapping("/enfant/progression/{enfantEmail}")
    public ResponseEntity<Map<String, Object>> getProgressionEnfant(@PathVariable String enfantEmail) {
        try {
            Map<String, Object> progression = parentService.getProgressionEnfant(enfantEmail);
            return ResponseEntity.ok(progression); // Retourne une réponse 200 avec la progression
        } catch (EntityNotFoundException e) {
            // Gère le cas où l'enfant n'est pas trouvé
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Enfant non trouvé"));
        } catch (SecurityException e) {
            // Gère le cas où le parent tente d'accéder à un enfant qui n'est pas sous sa supervision
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Accès refusé"));
        } catch (Exception e) {
            // Gère tout autre type d'exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Erreur interne du serveur"));
        }
    }


    @GetMapping("/getEnfantsByCurrentParent")
    public List<Enfant> getEnfantsByCurrentParent() {
        return parentService.getEnfantsByCurrentParent(); // Appel du service
    }

}
