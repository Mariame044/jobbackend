package odk.apprenant.jobaventure_backend.controller;


import odk.apprenant.jobaventure_backend.dtos.MetierDto;
import odk.apprenant.jobaventure_backend.model.*;
import odk.apprenant.jobaventure_backend.repository.TrancheageRepository;
import odk.apprenant.jobaventure_backend.service.InterviewService;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/interview")


public class InterviewController {

    @Autowired
    private InterviewService interviewService;

    @Autowired
    private MetierService metierService;
    @Autowired
    private TrancheageRepository trancheageRepository;
    @Autowired
    private StatistiqueService statistiqueService;

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
            String dateStr = request.getParameter("date");
            String description = request.getParameter("description");
            String titre = request.getParameter("titre");
            String metierIdStr = request.getParameter("metierId");
            String trancheageIdStr = request.getParameter("trancheageId");
            // Convertir la chaîne de date en objet Date
            Date date = parseDate(dateStr); // Méthode pour analyser la chaîne de date

            // Convertir java.util.Date en java.time.LocalDateTime
            LocalDateTime localDateTime = convertToLocalDateTime(date);

            // Conversion de l'ID de métier et récupération du métier
            Long metierId = Long.parseLong(metierIdStr);
            MetierDto metierDto = metierService.getMetier(metierId); // Récupération du métier
            Long trancheageId = Long.parseLong(trancheageIdStr);
            Trancheage trancheage = trancheageRepository.findById(trancheageId)
                    .orElseThrow(() -> new RuntimeException("Tranche d'âge non trouvée avec l'ID : " + trancheageId));


            // Créer une nouvelle instance de Interview
            Interview nouvelleInterview = new Interview();
            nouvelleInterview.setDuree(duree);
            nouvelleInterview.setDate(localDateTime); // Assigner la date convertie
            nouvelleInterview.setDescription(description);
            nouvelleInterview.setTitre(titre);
            nouvelleInterview.setTrancheage(trancheage); // Lier la tranche d'âge à la vidéo


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
        } catch (ParseException e) {
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

    // Méthode pour analyser la chaîne de date
    private Date parseDate(String dateStr) throws ParseException {
        // Définissez le format attendu de la date (ex: "yyyy-MM-dd'T'HH:mm:ss")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); // Format ISO 8601
        return dateFormat.parse(dateStr);
    }

    // Méthode pour convertir java.util.Date en java.time.LocalDateTime
    private LocalDateTime convertToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()); // Utilisez le fuseau horaire souhaité
    }
    @GetMapping("/metier/{metierId}")
    public ResponseEntity<List<Interview>> getInterviewsByMetierId(@PathVariable Long metierId) {
        List<Interview> interviews = interviewService.getInterviewsByMetierId(metierId);
        if (interviews.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(interviews);
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
        statistiqueService.incrementerVueInterview(id);
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
    // Méthode pour récupérer les vidéos d'un enfant
    @GetMapping("/enfantage")
    public ResponseEntity<List<Interview>> obtenirVideosPourEnfantConnecte() {
        try {
            List<Interview> interviewsPourEnfant = interviewService.trouverVideosPourEnfantParAge(); // Méthode ajustée
            return ResponseEntity.ok(interviewsPourEnfant);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/pour-enfant/metier/{metierId}")
    public ResponseEntity<List<Interview>> obtenirInterviewsParMetierEtAge(@PathVariable Long metierId) {
        try {
            List<Interview> interviews = interviewService.trouverInterviewParMetierEtAge(metierId); // Appel à la nouvelle méthode
            return ResponseEntity.ok(interviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Endpoint pour regarder une vidéo
    @GetMapping("/regarder/{id}")
    public Interview regarderInterview(@PathVariable Long id) {
        statistiqueService.incrementerVueVideo(id);
        return interviewService.interviewregardees(id);
    }

    @PostMapping("/poser") // Endpoint pour poser une question
    public ResponseEntity<String> poserQuestion(@RequestBody Question1 request) {
        try {
            // Vérification des paramètres requis
            if (request.getContenu() == null || request.getContenu().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Le contenu de la question ne peut pas être vide.");
            }
            if (request.getInterviewId() == null) {
                return ResponseEntity.badRequest().body("L'ID de l'interview est requis.");
            }

            // Appeler la méthode du service pour poser la question
            interviewService.poserQuestion(request.getInterviewId(), request.getContenu());

            // Retourner une réponse avec le code 201 Created
            return ResponseEntity.status(HttpStatus.CREATED).body("Question posée avec succès.");
        } catch (Exception e) {
            // Gérer les exceptions et retourner une réponse d'erreur
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la pose de la question : " + e.getMessage());
        }
    }

}