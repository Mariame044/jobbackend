package odk.apprenant.jobaventure_backend.controller;


import odk.apprenant.jobaventure_backend.dtos.CategorieDto;
import odk.apprenant.jobaventure_backend.model.Categorie;
import odk.apprenant.jobaventure_backend.service.CategorieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController

@RequestMapping("/api/categories")
@PreAuthorize("hasRole('ADMIN')")
public class CategorieController {

    @Autowired
    private CategorieService categorieService;

    // Créer une nouvelle catégorie
    @PostMapping("/creer")
    public ResponseEntity<CategorieDto> createCategorie(@RequestBody CategorieDto categorieDto) {
        CategorieDto createdCategorie = categorieService.createCategorie(categorieDto);
        return ResponseEntity.status(201).body(createdCategorie); // 201 Created
    }


    // Mettre à jour une catégorie
    @PutMapping("/{id}")
    public ResponseEntity<CategorieDto> updateCategorie(@PathVariable Long id, @RequestBody CategorieDto updatedCategorieDto) {
        CategorieDto updatedCategorie = categorieService.updateCategorie(id, updatedCategorieDto);
        return ResponseEntity.ok(updatedCategorie); // Retourne 200 OK
    }

    // Supprimer une catégorie
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategorie(@PathVariable Long id) {
        categorieService.deleteCategorie(id);
        return ResponseEntity.noContent().build(); // Retourne 204 No Content
    }
}