package odk.apprenant.jobaventure_backend.controller;


import odk.apprenant.jobaventure_backend.dtos.MetierDto;
import odk.apprenant.jobaventure_backend.model.Interview;
import odk.apprenant.jobaventure_backend.model.Metier;
import odk.apprenant.jobaventure_backend.model.Video;
import odk.apprenant.jobaventure_backend.service.InterviewService;
import odk.apprenant.jobaventure_backend.service.MetierService;
import odk.apprenant.jobaventure_backend.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/interview")
public class InterviewController {

    @Autowired
    private InterviewService interviewService;

    @Autowired
    private MetierService metierService;

    // Endpoint pour ajouter une nouvelle interview
    @PostMapping
    public ResponseEntity<Interview> ajouterInterview(MultipartHttpServletRequest request) {
        try {
            // Extraire le fichier vidéo
            MultipartFile fichier = request.getFile("fichier");
            if (fichier == null || fichier.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // Extraire les autres paramètres
            String duree = request.getParameter("duree");
            String date = request.getParameter("date");
            String description = request.getParameter("description");
            String metierIdStr = request.getParameter("metierId");

            // Conversion de l'ID de métier et récupération du métier
            Long metierId = Long.parseLong(metierIdStr);
            MetierDto metierDto = metierService.getMetier(metierId); // Récupération du métier

            // Créer une nouvelle instance de Interview
            Interview nouvelleInterview = new Interview();
            nouvelleInterview.setDuree(duree);
            nouvelleInterview.setDate(date);
            nouvelleInterview.setDescription(description);
            nouvelleInterview.setMetier(convertToEntity(metierDto)); // Convertir MetierDto en Metier

            // Ajouter l'interview via le service
            Interview interviewAjoutee = interviewService.ajouterInterview(nouvelleInterview, fichier);

            // Définir l'URL
            String url = "http://localhost:8080/uploads/videos/" + fichier.getOriginalFilename();
            interviewAjoutee.setUrl(url); // Ajouter l'URL au modèle

            return new ResponseEntity<>(interviewAjoutee, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NumberFormatException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
    // Récupérer toutes les interviews
    @GetMapping
    public ResponseEntity<List<Interview>> obtenirToutesLesInterviews() {
        List<Interview> interviews = interviewService.trouverToutesLesInterview();
        return new ResponseEntity<>(interviews, HttpStatus.OK);
    }

    // Récupérer une interview par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Interview> obtenirInterviewParId(@PathVariable Long id) {
        Optional<Interview> interview = interviewService.trouverInterviewParId(id);
        return interview.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Modifier une interview existante
    @PutMapping("/{id}")
    public ResponseEntity<Interview> modifierInterview(
            @PathVariable Long id,
            @RequestBody Interview interview
    ) {
        Interview interviewModifiee = interviewService.modifierInterview(id, interview);
        if (interviewModifiee != null) {
            return new ResponseEntity<>(interviewModifiee, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Supprimer une interview par son ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerInterview(@PathVariable Long id) {
        interviewService.supprimerInterview(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}