package odk.apprenant.jobaventure_backend.controller;


import odk.apprenant.jobaventure_backend.dtos.MetierDto;
import odk.apprenant.jobaventure_backend.model.Categorie;
import odk.apprenant.jobaventure_backend.model.Metier;
import odk.apprenant.jobaventure_backend.model.Video;
import odk.apprenant.jobaventure_backend.service.MetierService;
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
            String metierIdStr = request.getParameter("metierId");

            // Conversion de l'ID de métier et récupération du métier
            // Conversion de l'ID de métier et récupération du métier
            Long metierId = Long.parseLong(metierIdStr);
            MetierDto metierDto = metierService.getMetier(metierId); // Utilisation de MetierDto


            // Créer une nouvelle instance de Video
            Video nouvelleVideo = new Video();
            nouvelleVideo.setDuree(duree);
            nouvelleVideo.setDescription(description);
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
        return videoService.regarderVideo(id);
    }
}