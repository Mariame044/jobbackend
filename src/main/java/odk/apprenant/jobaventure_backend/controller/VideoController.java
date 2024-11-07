package odk.apprenant.jobaventure_backend.controller;


import odk.apprenant.jobaventure_backend.dtos.MetierDto;
import odk.apprenant.jobaventure_backend.model.*;
import odk.apprenant.jobaventure_backend.repository.EnfanrRepository;
import odk.apprenant.jobaventure_backend.repository.TrancheageRepository;
import odk.apprenant.jobaventure_backend.repository.VideoRepository;
import odk.apprenant.jobaventure_backend.service.MetierService;
import odk.apprenant.jobaventure_backend.service.StatistiqueService;
import odk.apprenant.jobaventure_backend.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/videos")
@PreAuthorize("hasRole('ADMIN')")
public class VideoController {

    @Autowired
    private VideoService videoService;
    @Autowired
    private MetierService metierService;
    @Autowired
    private TrancheageRepository trancheageRepository;
    @Autowired
    private EnfanrRepository enfanrRepository;
    @Autowired
    private StatistiqueService statistiqueService;

    @PostMapping
    public ResponseEntity<Video> ajouterVideo(MultipartHttpServletRequest request) {
        try {
            // Extraire le fichier
            MultipartFile fichier = request.getFile("fichier");
            if (fichier == null || fichier.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // Extraire les autres paramètres
            String duree = request.getParameter("duree");
            String description = request.getParameter("description");
            String titre = request.getParameter("titre");
            String metierIdStr = request.getParameter("metierId");
            String trancheageIdStr = request.getParameter("trancheageId");

            // Conversion de l'ID de métier et récupération du métier
            // Conversion de l'ID de métier et récupération du métier
            Long metierId = Long.parseLong(metierIdStr);
            MetierDto metierDto = metierService.getMetier(metierId); // Utilisation de MetierDto
            Long trancheageId = Long.parseLong(trancheageIdStr);
            Trancheage trancheage = trancheageRepository.findById(trancheageId)
                    .orElseThrow(() -> new RuntimeException("Tranche d'âge non trouvée avec l'ID : " + trancheageId));



            // Créer une nouvelle instance de Video
            Video nouvelleVideo = new Video();
            nouvelleVideo.setTitre(titre);
            nouvelleVideo.setDescription(description);
            nouvelleVideo.setTitre(titre);
            nouvelleVideo.setTrancheage(trancheage); // Lier la tranche d'âge à la vidéo

            //nouvelleVideo.setMetier(metierDto); // Lier le métier à la vidéo
            nouvelleVideo.setMetier(convertToEntity(metierDto)); // Convertir MetierDto en Metier

            // Ajouter la vidéo via le service
            Video videoAjoutee = videoService.ajouterVideo(nouvelleVideo, fichier);

            // Définir l'URL
            String url = "http://localhost:8080/uploads/videos/" + fichier.getOriginalFilename();
            videoAjoutee.setUrl(url); // Ajouter l'URL au modèle

            return new ResponseEntity<>(videoAjoutee, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Méthode pour convertir MetierDto en Metier
    private Metier convertToEntity(MetierDto metierDto) {
        Metier metier = new Metier();
        metier.setId(metierDto.getId());
        metier.setNom(metierDto.getNom());
        metier.setDescription(metierDto.getDescription());
        // Assurez-vous d'ajouter d'autres attributs de Metier si nécessaire
        return metier;
    }

    // Récupérer toutes les vidéos
    @GetMapping
    public ResponseEntity<List<Video>> obtenirToutesLesVideos() {
        List<Video> videos = videoService.trouverToutesLesVideos();
        return new ResponseEntity<>(videos, HttpStatus.OK);
    }

    // Récupérer une vidéo par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Video> obtenirVideoParId(@PathVariable Long id) {
        Optional<Video> video = videoService.trouverVideoParId(id);
        return video.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    // Modifier une vidéo existante
    @PutMapping("/{id}")
    public ResponseEntity<Video> modifierVideo(
            @PathVariable Long id,
            @RequestBody Video video
    ) {
        Video videoModifiee = videoService.modifierVideo(id, video);
        if (videoModifiee != null) {
            return new ResponseEntity<>(videoModifiee, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Supprimer une vidéo par son ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerVideo(@PathVariable Long id) {
        videoService.supprimerVideo(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    // Endpoint pour regarder une vidéo
    @GetMapping("/regarder/{id}")
    public Video regarderVideo(@PathVariable Long id) {
        statistiqueService.incrementerVueVideo(id);
        return videoService.regarderVideo(id);
    }


    // Méthode pour récupérer les vidéos par métier en utilisant l'ID du métier
    //@GetMapping("/metier/{metierId}")
    //public List<Video> obtenirVideosParMetier(@PathVariable Long metierId) {
        //return videoService.trouverVideosParMetierId(metierId);
    //}
    // Méthode pour récupérer les vidéos d'un enfant
    @GetMapping("/pour-enfant")
    public ResponseEntity<List<Video>> obtenirVideosPourEnfantConnecte() {
        try {
            List<Video> videosPourEnfant = videoService.trouverVideosPourEnfantParAge(); // Méthode ajustée
            return ResponseEntity.ok(videosPourEnfant);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/pour-enfant/metier/{metierId}")
    public ResponseEntity<List<Video>> obtenirVideosParMetierEtAge(@PathVariable Long metierId) {
        try {
            List<Video> videos = videoService.trouverVideosParMetierEtAge(metierId); // Appel à la nouvelle méthode
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}