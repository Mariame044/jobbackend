package odk.apprenant.jobaventure_backend.service;


import odk.apprenant.jobaventure_backend.model.Admin;
import odk.apprenant.jobaventure_backend.model.Enfant;
import odk.apprenant.jobaventure_backend.model.Video;
import odk.apprenant.jobaventure_backend.repository.AdminRepository;
import odk.apprenant.jobaventure_backend.repository.EnfanrRepository;
import odk.apprenant.jobaventure_backend.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
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
    List<Video> trouverVideosParMetierEtAge(Long metierId);
    //List<Video> trouverVideosParMetierId(Long metierId);
    List<Video> trouverVideosPourEnfantParAge();
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
    @Autowired
    private EnfanrRepository enfanrRepository;

    // Méthode pour obtenir l'administrateur connecté
    private Admin getCurrentAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Supposons que l'email est utilisé comme principal
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Administrateur non trouvé"));
    }
    private Enfant getCurrentEnfant() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Supposons que l'email est utilisé comme principal

        // Rechercher l'enfant par son email
        return enfanrRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("L'enfant avec l'email " + email + " n'a pas été trouvé"));
    }
    @Override
    public Video ajouterVideo(Video video, MultipartFile fichier) throws IOException {

        if (video.getMetier() == null) {
            throw new RuntimeException("La METIER est obligatoire.");
        }
        if (video.getTrancheage() == null) {
            throw new RuntimeException("La tranche age est obligatoire.");
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
    public List<Video> trouverVideosPourEnfantParAge() {
        Enfant enfant = getCurrentEnfant(); // Récupère l'enfant connecté
        int ageEnfant = enfant.getAge();    // Récupère l'âge de l'enfant

        // Récupère toutes les vidéos
        List<Video> toutesLesVideos = videoRepository.findAll();
        List<Video> videosFiltrees = new ArrayList<>();

        // Filtrer les vidéos par tranche d'âge
        for (Video video : toutesLesVideos) {
            if (video.getTrancheage() != null) {
                int ageMin = video.getTrancheage().getAgeMin();
                int ageMax = video.getTrancheage().getAgeMax();
                if (ageEnfant >= ageMin && ageEnfant <= ageMax) {
                    videosFiltrees.add(video);
                }
            }
        }
        return videosFiltrees; // Retourne les vidéos filtrées
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
            v.setTitre(video.getTitre());
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

            // Enregistrez les modifications de la vidéo
            videoRepository.save(video);

            // Obtenez l'enfant connecté
            Enfant enfant = getCurrentEnfant(); // Méthode pour obtenir l'enfant connecté

            // Vérifiez si la vidéo a déjà été regardée
            if (!enfant.hasWatchedVideo(video)) {
                // Si la vidéo n'a pas été regardée, ajoutez-la à l'enfant
                enfant.addVideoRegardee(video); // Ajoutez la vidéo à l'enfant

                // Enregistrez les modifications de l'enfant
                enfanrRepository.save(enfant);
            } else {
                // Optionnel : gérer le cas où la vidéo a déjà été regardée
                System.out.println("Cette vidéo a déjà été regardée par l'enfant.");
            }

            return video; // Retourne la vidéo pour visionnage
        } else {
            throw new RuntimeException("Vidéo non trouvée avec l'ID : " + id);
        }
    }


    @Override
    public void supprimerVideo(Long id) {
        videoRepository.deleteById(id);
    }
    //@Override
    //public List<Video> trouverVideosParMetierId(Long metierId) {
        //return videoRepository.findByMetierId(metierId);
    //}
    @Override
    public List<Video> trouverVideosParMetierEtAge(Long metierId) {
        // Récupérer l'enfant connecté
        Enfant enfant = getCurrentEnfant();
        int ageEnfant = enfant.getAge();

        // Récupérer toutes les vidéos par métier
        List<Video> videosParMetier = videoRepository.findByMetierId(metierId);
        List<Video> videosFiltrees = new ArrayList<>();

        // Filtrer les vidéos en fonction de la tranche d'âge de l'enfant
        for (Video video : videosParMetier) {
            if (video.getTrancheage() != null) {
                int ageMin = video.getTrancheage().getAgeMin();
                int ageMax = video.getTrancheage().getAgeMax();
                if (ageEnfant >= ageMin && ageEnfant <= ageMax) {
                    videosFiltrees.add(video);
                }
            }
        }

        return videosFiltrees; // Retourne les vidéos filtrées par métier et tranche d'âge
    }

}