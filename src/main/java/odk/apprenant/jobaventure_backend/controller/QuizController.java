package odk.apprenant.jobaventure_backend.controller;

import odk.apprenant.jobaventure_backend.model.Quiz;
import odk.apprenant.jobaventure_backend.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    // Récupérer tous les quizzes
    @GetMapping
    public ResponseEntity<List<Quiz>> getAllQuizzes() {
        List<Quiz> quizzes = quizService.getAllQuizzes();
        return new ResponseEntity<>(quizzes, HttpStatus.OK);
    }

    // Récupérer un quiz par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Quiz> getQuizById(@PathVariable Long id) {
        Optional<Quiz> quiz = quizService.getQuizById(id);
        return quiz.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Créer un nouveau quiz
    @PostMapping
    public ResponseEntity<Quiz> createQuiz(@RequestBody Quiz quiz,
                                           @RequestParam(required = false) Long badgeId,
                                           @RequestParam(required = false) Long metierId) {
        Quiz createdQuiz = quizService.createQuiz(quiz, badgeId, metierId);
        return new ResponseEntity<>(createdQuiz, HttpStatus.CREATED);
    }

    // Mettre à jour un quiz existant
    @PutMapping("/{id}")
    public ResponseEntity<Quiz> updateQuiz(@PathVariable Long id, @RequestBody Quiz quizDetails) {
        try {
            Quiz updatedQuiz = quizService.updateQuiz(id, quizDetails);
            return new ResponseEntity<>(updatedQuiz, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Supprimer un quiz
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        try {
            quizService.deleteQuiz(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}