package odk.apprenant.jobaventure_backend.controller;


import odk.apprenant.jobaventure_backend.model.Parent;
import odk.apprenant.jobaventure_backend.service.ParentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
}
