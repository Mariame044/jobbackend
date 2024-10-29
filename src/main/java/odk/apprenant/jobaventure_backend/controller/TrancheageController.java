package odk.apprenant.jobaventure_backend.controller;


import odk.apprenant.jobaventure_backend.model.Trancheage;
import odk.apprenant.jobaventure_backend.service.TrancheageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/age")
public class TrancheageController {
    @Autowired
    TrancheageService trancheageService;


    // Endpoint pour créer une nouvelle tranche d'âge
    @PostMapping
    public ResponseEntity<Trancheage> createTrancheAge(@RequestBody Trancheage trancheage) {
        Trancheage createdTrancheAge = trancheageService.createTrancheage(trancheage);
        return ResponseEntity.ok(createdTrancheAge);
    }

    // Endpoint pour récupérer toutes les tranches d'âge
    @GetMapping
    public ResponseEntity<List<Trancheage>> getAllTrancheAges() {
        List<Trancheage> trancheAges = trancheageService.getAllTrancheages();
        return ResponseEntity.ok(trancheAges);
    }

    // Endpoint pour récupérer une tranche d'âge par ID
    @GetMapping("/{id}")
    public ResponseEntity<Trancheage> getTrancheAgeById(@PathVariable Long id) {
        return trancheageService.getTrancheageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint pour mettre à jour une tranche d'âge
    @PutMapping("/update/{id}")
    public ResponseEntity<Trancheage> updateTrancheAge(@PathVariable Long id, @RequestBody Trancheage trancheage) {
        return trancheageService.updateTrancheage(id, trancheage)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint pour supprimer une tranche d'âge
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTrancheAge(@PathVariable Long id) {
        trancheageService.deleteTrancheageById(id);
        return ResponseEntity.noContent().build();
    }
}