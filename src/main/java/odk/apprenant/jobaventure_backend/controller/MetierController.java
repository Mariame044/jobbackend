package odk.apprenant.jobaventure_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import odk.apprenant.jobaventure_backend.model.Categorie;
import odk.apprenant.jobaventure_backend.model.Metier;
import odk.apprenant.jobaventure_backend.service.CategorieService;
import odk.apprenant.jobaventure_backend.service.MetierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/metiers")
public class MetierController {

    @Autowired
    private MetierService metierService;
    @Autowired
    private CategorieService categorieService;

    @PostMapping( "/ajout")
    public ResponseEntity<Metier> creerMetier(MultipartHttpServletRequest request) {
        try {
            // Extraire les autres paramètres
            String nom = request.getParameter("nom");
            String description = request.getParameter("description");
            String categorieIdStr = request.getParameter("categorieId");

            // Conversion de l'ID de catégorie et récupération de la catégorie
            Long categorieId = Long.parseLong(categorieIdStr);
            Optional<Categorie> categorieOpt = categorieService.getCategorieById(categorieId);

            if (!categorieOpt.isPresent()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Catégorie non trouvée
            }

            // Créer une nouvelle instance de Metier
            Metier nouveauMetier = new Metier();
            nouveauMetier.setNom(nom);
            nouveauMetier.setDescription(description);
            nouveauMetier.setCategorie(categorieOpt.get());

            // Extraire le fichier image si présent
            MultipartFile image = request.getFile("image");

            // Ajouter le métier via le service
            Metier metierAjoute = metierService.creerMetier(nouveauMetier, image);

            return new ResponseEntity<>(metierAjoute, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<Metier> modifierMetier(
            @PathVariable Long id,
            @RequestPart(value = "metier") String detailsMetierJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            // Convertir le JSON en objet Metier
            Metier detailsMetier = new ObjectMapper().readValue(detailsMetierJson, Metier.class);
            Metier metierMisAJour = metierService.modifierMetier(id, detailsMetier, image);
            return ResponseEntity.ok(metierMisAJour);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null); // Erreur de sauvegarde d'image
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Metier> getMetier(@PathVariable Long id) {
        Metier metier = metierService.getMetier(id);
        return ResponseEntity.ok(metier);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerMetier(@PathVariable Long id) {
        try {
            metierService.supprimerMetier(id);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            return ResponseEntity.status(500).build(); // Erreur de suppression d'image
        }
    }
}
