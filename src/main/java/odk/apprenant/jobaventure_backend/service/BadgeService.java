package odk.apprenant.jobaventure_backend.service;


import odk.apprenant.jobaventure_backend.model.Badge;
import odk.apprenant.jobaventure_backend.repository.BadgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BadgeService {

    @Autowired
    private BadgeRepository badgeRepository;

    // Obtenir tous les badges
    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }

    // Obtenir un badge par ID
    public Optional<Badge> getBadgeById(Long id) {
        return badgeRepository.findById(id);
    }

    // Créer un nouveau badge
    public Badge createBadge(Badge badge) {
        return badgeRepository.save(badge);
    }



    // Supprimer un badge
    public void deleteBadge(Long id) {
        badgeRepository.deleteById(id);
    }
}

