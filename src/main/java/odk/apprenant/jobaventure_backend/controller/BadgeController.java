package odk.apprenant.jobaventure_backend.controller;


import odk.apprenant.jobaventure_backend.model.Badge;
import odk.apprenant.jobaventure_backend.service.BadgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/badges")
public class BadgeController {

    @Autowired
    private BadgeService badgeService;

    // Obtenir tous les badges
    @GetMapping
    public List<Badge> getAllBadges() {
        return badgeService.getAllBadges();
    }

    // Obtenir un badge par ID
    @GetMapping("/{id}")
    public ResponseEntity<Badge> getBadgeById(@PathVariable Long id) {
        Optional<Badge> badge = badgeService.getBadgeById(id);
        return badge.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Créer un nouveau badge
    @PostMapping
    public ResponseEntity<Badge> createBadge(@RequestBody Badge badge) {
        Badge createdBadge = badgeService.createBadge(badge);
        return ResponseEntity.ok(createdBadge);
    }


    // Supprimer un badge
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBadge(@PathVariable Long id) {
        badgeService.deleteBadge(id);
        return ResponseEntity.noContent().build();
    }
}