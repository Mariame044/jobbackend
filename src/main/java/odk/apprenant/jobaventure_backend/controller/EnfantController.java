package odk.apprenant.jobaventure_backend.controller;


import odk.apprenant.jobaventure_backend.dtos.CategorieDto;
import odk.apprenant.jobaventure_backend.dtos.MetierDto;
import odk.apprenant.jobaventure_backend.model.Enfant;
import odk.apprenant.jobaventure_backend.model.Metier;
import odk.apprenant.jobaventure_backend.service.CategorieService;
import odk.apprenant.jobaventure_backend.service.EnfantService;
import odk.apprenant.jobaventure_backend.service.MetierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/enfants")

public class EnfantController {

    @Autowired
    private EnfantService enfantService; // Injection du service
    @Autowired
    private CategorieService categorieService;
    @Autowired
    private MetierService metierService; // Injection du service

    @PostMapping("/registerenfant")
    public ResponseEntity<Enfant> registerEnfant(@RequestBody Enfant enfant) {
        try {
            Enfant savedEnfant = enfantService.registerEnfant(enfant);
            return ResponseEntity.ok(savedEnfant);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Récupérer toutes les catégories
    @GetMapping("liste")

    public ResponseEntity<List<CategorieDto>> getAllCategories() {
        List<CategorieDto> categories = categorieService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    // Récupérer une catégorie par ID
    @GetMapping("/{id1}")

    public ResponseEntity<CategorieDto> getCategorieById(@PathVariable Long id) {
        return categorieService.getCategorieById(id)
                .map(ResponseEntity::ok) // Retourne 200 OK si la catégorie est trouvée
                .orElse(ResponseEntity.notFound().build()); // Retourne 404 Not Found si non trouvée
    }
    @GetMapping("/{id}")

    public ResponseEntity<MetierDto> getMetier(@PathVariable Long id) {
        MetierDto metier = metierService.getMetier(id);
        return ResponseEntity.ok(metier);
    }
    // Récupérer tous les métiers
    @GetMapping

    public ResponseEntity<List<Metier>> getAllMetiers() {
        List<Metier> metiers = metierService.getAllMetiers();


        return ResponseEntity.ok(metiers);
    }
    @GetMapping("/uploads/images/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            // Spécifiez le chemin d'accès à l'image
            Path imagePath = Paths.get("C:/Users/mariame.daou/Documents/projet de fin/jobaventure_backend/uploads/images/").resolve(filename).normalize();
            Resource resource = new UrlResource(imagePath.toUri());

            // Vérifiez si le fichier existe et est lisible
            if (resource.exists() && resource.isReadable()) {
                // Déterminez le type MIME de l'image
                String mimeType = determineMimeType(filename);
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(mimeType)) // Utilisation du type MIME dynamique
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found si le fichier n'existe pas
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error en cas de problème de lecture
        }
    }

    private String determineMimeType(String filename) {
        if (filename.endsWith(".png")) {
            return "image/png";
        } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filename.endsWith(".gif")) {
            return "image/gif";
        }
        // Ajoutez d'autres types d'images si nécessaire
        return "application/octet-stream"; // type par défaut pour les fichiers non reconnus
    }

}
