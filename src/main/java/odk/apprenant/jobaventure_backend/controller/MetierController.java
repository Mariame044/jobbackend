package odk.apprenant.jobaventure_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import odk.apprenant.jobaventure_backend.dtos.CategorieDto;

import odk.apprenant.jobaventure_backend.dtos.ErrorResponse;
import odk.apprenant.jobaventure_backend.dtos.MetierDto;
import odk.apprenant.jobaventure_backend.model.Metier;
import odk.apprenant.jobaventure_backend.service.CategorieService;
import odk.apprenant.jobaventure_backend.service.MetierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/metiers")

public class MetierController {

    @Autowired
    private MetierService metierService;
    @Autowired
    private CategorieService categorieService;
    @PostMapping("/ajout")
    public ResponseEntity<?> creerMetier(MultipartHttpServletRequest request) {
        try {
            // Extraction des paramètres
            String nom = request.getParameter("nom");
            String description = request.getParameter("description");
            String categorieIdStr = request.getParameter("categorieId");

            // Vérification des champs obligatoires
            if (nom == null || nom.isEmpty()) {
                return ResponseEntity.badRequest().body("Le nom est requis.");
            }
            if (description == null || description.isEmpty()) {
                return ResponseEntity.badRequest().body("La description est requise.");
            }
            if (categorieIdStr == null || categorieIdStr.isEmpty()) {
                return ResponseEntity.badRequest().body("L'ID de catégorie est requis.");
            }

            // Conversion de l'ID de catégorie
            Long categorieId;
            try {
                categorieId = Long.parseLong(categorieIdStr);
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("Mauvais format d'ID de catégorie.");
            }

            // Vérification de l'existence de la catégorie
            Optional<CategorieDto> categorieOpt = categorieService.getCategorieById(categorieId);
            if (!categorieOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Catégorie non trouvée.");
            }

            // Création d'une nouvelle instance de MetierDto
            MetierDto nouveauMetierDto = new MetierDto();
            nouveauMetierDto.setNom(nom);
            nouveauMetierDto.setDescription(description);
            nouveauMetierDto.setCategorie(categorieOpt.get());

            // Extraction de l'image si présente
            MultipartFile image = request.getFile("image");

            // Ajout du métier via le service
            MetierDto metierAjoute = metierService.creerMetier(nouveauMetierDto, image);
            return ResponseEntity.status(HttpStatus.CREATED).body(metierAjoute);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur.");
        }
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<MetierDto> modifierMetier(
            @PathVariable Long id,
            @RequestPart(value = "metier") String detailsMetierJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            // Convertir le JSON en objet MetierDto
            MetierDto detailsMetier = new ObjectMapper().readValue(detailsMetierJson, MetierDto.class);
            MetierDto metierMisAJour = metierService.modifierMetier(id, detailsMetier, image);
            return ResponseEntity.ok(metierMisAJour);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Erreur de sauvegarde d'image
        }
    }




    // Supprimer une catégorie
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMerier(@PathVariable Long id) {
        metierService.deleteMetier(id);
        return ResponseEntity.noContent().build(); // Retourne 204 No Content
    }

}