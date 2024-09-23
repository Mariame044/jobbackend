package odk.apprenant.jobaventure_backend.controller;


import odk.apprenant.jobaventure_backend.model.Enfant;
import odk.apprenant.jobaventure_backend.service.EnfantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enfants")
public class EnfantController {

    @Autowired
    private EnfantService enfantService; // Injection du service

    @PostMapping("/registerenfant")
    public ResponseEntity<Enfant> registerEnfant(@RequestBody Enfant enfant) {
        try {
            Enfant savedEnfant = enfantService.registerEnfant(enfant);
            return ResponseEntity.ok(savedEnfant);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

}
