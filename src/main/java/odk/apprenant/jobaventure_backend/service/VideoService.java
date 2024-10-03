package odk.apprenant.jobaventure_backend.service;


import odk.apprenant.jobaventure_backend.model.Admin;
import odk.apprenant.jobaventure_backend.model.Video;
import odk.apprenant.jobaventure_backend.repository.AdminRepository;
import odk.apprenant.jobaventure_backend.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface VideoService {

    // Méthode pour ajouter une vidéo
    Video ajouterVideo(Video video, MultipartFile fichier) throws IOException;

    // Méthode pour récupérer toutes les vidéos
    List<Video> trouverToutesLesVideos();

    // Méthode pour trouver une vidéo par ID
    Optional<Video> trouverVideoParId(Long id);

    // Méthode pour modifier une vidéo
    Video modifierVideo(Long id, Video video);

    // Méthode pour supprimer une vidéo
    void supprimerVideo(Long id);

    // Méthode pour regarder une vidéo
    Video regarderVideo(Long id);
}

// Implémentation du service VideoService
@Service
class VideoServiceImpl implements VideoService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private AdminRepository adminRepository;
    // Méthode pour obtenir l'administrateur connecté
    private Admin getCurrentAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Supposons que l'email est utilisé comme principal
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Administrateur non trouvé"));
    }
    @Override
    public Video ajouterVideo(Video video, MultipartFile fichier) throws IOException {

        if (video.getMetier() == null) {
            throw new RuntimeException("La METIER est obligatoire.");
        }
        // Sauvegarde le fichier vidéo
        String cheminVideo = fileStorageService.sauvegarderVideo(fichier);
        video.setUrl(cheminVideo); // Définit l'URL ou le chemin de la vidéo
        Admin admin = getCurrentAdmin();
        video.setAdmin(admin); // Enregi
        return videoRepository.save(video);
    }

    @Override
    public List<Video> trouverToutesLesVideos() {
        return videoRepository.findAll();
    }

    @Override
    public Optional<Video> trouverVideoParId(Long id) {
        return videoRepository.findById(id);
    }

    @Override
    public Video modifierVideo(Long id, Video video) {
        Optional<Video> videoExistante = videoRepository.findById(id);
        if (videoExistante.isPresent()) {
            Video v = videoExistante.get();
            v.setDuree(video.getDuree());
            v.setDescription(video.getDescription());
            v.setAdmin(video.getAdmin());
            v.setMetier(video.getMetier());
            return videoRepository.save(v);
        } else {
            return null;
        }
    }
    @Override
    public Video regarderVideo(Long id) {
        Optional<Video> videoOptional = videoRepository.findById(id);
        if (videoOptional.isPresent()) {
            Video video = videoOptional.get();

            // Incrémentez le nombre de vues
            video.setNombreDeVues(video.getNombreDeVues() + 1);

            // Enregistrez les modifications
            videoRepository.save(video);

            return video; // Retourne la vidéo pour visionnage
        } else {
            throw new RuntimeException("Vidéo non trouvée avec l'ID : " + id);
        }
    }


    @Override
    public void supprimerVideo(Long id) {
        videoRepository.deleteById(id);
    }
}