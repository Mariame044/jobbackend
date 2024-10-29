package odk.apprenant.jobaventure_backend.controller;

import odk.apprenant.jobaventure_backend.dtos.QuizDto;
import odk.apprenant.jobaventure_backend.model.Question;
import odk.apprenant.jobaventure_backend.model.Quiz;
import odk.apprenant.jobaventure_backend.model.Video;
import odk.apprenant.jobaventure_backend.repository.QuizRepository;
import odk.apprenant.jobaventure_backend.service.QuizService;
import odk.apprenant.jobaventure_backend.service.StatistiqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;
    @Autowired
    private StatistiqueService statistiqueService;

    // Récupérer tous les quizzes
    @GetMapping
    public ResponseEntity<List<QuizDto>> getAllQuizzes() {
        List<QuizDto> quizzes = quizService.getAllQuizzes();
        return ResponseEntity.ok(quizzes); // Retourne une réponse HTTP 200 avec la liste
    }
    // Récupérer un quiz par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Quiz> getQuizById(@PathVariable Long id) {
        return quizService.getQuizById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Créer un nouveau quiz
    @PostMapping
    public ResponseEntity<Quiz> createQuiz(@RequestBody QuizDto quizDto) {
        Quiz createdQuiz = quizService.createQuiz(quizDto);
        return new ResponseEntity<>(createdQuiz, HttpStatus.CREATED);
    }

    // Mettre à jour un quiz existant
    @PutMapping("/{id}")
    public ResponseEntity<Quiz> updateQuiz(@PathVariable Long id, @RequestBody Quiz quizDetails) {
        try {
            Quiz updatedQuiz = quizService.updateQuiz(id, quizDetails);
            return ResponseEntity.ok(updatedQuiz);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Supprimer un quiz
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        try {
            quizService.deleteQuiz(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @GetMapping("/{quizId}/jouer") // Annotation pour gérer l'URL /api/jeux/{jeuId}/jouer
    public List<Question> jouer(@PathVariable Long quizId) {
        statistiqueService.incrementerSuccesQuiz(quizId);
        return quizService.jouer(quizId); // Appel de la méthode dans le service
    }
    // Vérifier la réponse d'une question
    @PostMapping("/{quizId}/questions/{questionId}/verifier")
    public ResponseEntity<Map<String, String>> verifierReponse(
            @PathVariable Long quizId,
            @PathVariable Long questionId,
            @RequestBody Map<String, String> body) {
        String reponseDonnee = body.get("reponseDonnee");
        String resultat = quizService.verifierReponse(quizId, questionId, reponseDonnee);

        // Renvoyer la réponse au format JSON
        Map<String, String> response = new HashMap<>();
        response.put("message", resultat); // Le message que vous souhaitez renvoyer
        return ResponseEntity.ok(response);
    }



    // Calculer le score basé sur les réponses données
    @PostMapping("/{jeuId}/calculerScore")
    public ResponseEntity<Integer> calculerScore(
            @PathVariable Long quizId,
            @RequestBody Map<Long, String> reponsesDonnees) {
        int score = quizService.calculerScore(quizId, reponsesDonnees);
        return ResponseEntity.ok(score);
    }
    @GetMapping("/pour-enfant/metier/{metierId}")
    public ResponseEntity<List<Quiz>> obtenirVideosParMetierEtAge(@PathVariable Long metierId) {
        try {
            List<Quiz> quizzes = quizService.trouverQuizParMetierEtAge(metierId); // Appel à la nouvelle méthode
            return ResponseEntity.ok(quizzes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    // Méthode pour récupérer les vidéos d'un enfant
    @GetMapping("/pour-enfant")
    public ResponseEntity<List<Quiz>> obtenirquizPourEnfantConnecte() {
        try {
            List<Quiz> quizPourEnfant = quizService.trouverQuizPourEnfantParAge(); // Méthode ajustée
            return ResponseEntity.ok(quizPourEnfant);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
