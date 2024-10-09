package odk.apprenant.jobaventure_backend.controller;


import odk.apprenant.jobaventure_backend.dtos.QuestionDto;
import odk.apprenant.jobaventure_backend.model.Jeuderole;
import odk.apprenant.jobaventure_backend.model.Question;
import odk.apprenant.jobaventure_backend.model.TypeQuestion;
import odk.apprenant.jobaventure_backend.repository.JeuderoleRepository;
import odk.apprenant.jobaventure_backend.repository.QuestionRepository;
import odk.apprenant.jobaventure_backend.service.JeuderoleService;
import odk.apprenant.jobaventure_backend.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;
    @Autowired
    private JeuderoleRepository jeuderoleRepository;
    @Autowired
    private QuestionRepository questionRepository;

    @GetMapping
    public List<Question> getAllQuestions() {
        return questionService.getAllQuestions();
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Question> createQuestion(@RequestBody QuestionDto questionDto) {
        // Validation des données entrantes
        if (questionDto.getPoint() == null || questionDto.getTexte() == null || questionDto.getTypeQuestion() == null) {
            throw new RuntimeException("Les champs point, texte et typeQuestion sont obligatoires.");
        }

        Question question = new Question();
        question.setPoint(questionDto.getPoint());
        question.setTexte(questionDto.getTexte());

        // Convertir le String en TypeQuestion
        try {
            TypeQuestion type = TypeQuestion.valueOf(questionDto.getTypeQuestion());
            question.setTypeQuestion(type);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Type de question invalide : " + questionDto.getTypeQuestion());
        }

        // Passer les IDs au service
        Question createdQuestion = questionService.createQuestion(
                question,
                questionDto.getQuizId(),      // ID du quiz
                questionDto.getJeuDeRoleId(), // ID du jeu de rôle
                questionDto.getReponseId()     // ID de la réponse
        );

        return ResponseEntity.ok(createdQuestion);
    }

    // Mettre à jour une question existante
    @PutMapping("/{id}")
    public ResponseEntity<Question> updateQuestion(@PathVariable Long id,
                                                   @RequestBody Question questionDetails,
                                                   @RequestParam(required = false) Long quizId,
                                                   @RequestParam(required = false) Long jeuDeRoleId,
                                                   @RequestParam(required = false) Long reponseId) {
        Question updatedQuestion = questionService.updateQuestion(id, questionDetails, quizId, jeuDeRoleId, reponseId);
        return ResponseEntity.ok(updatedQuestion);
    }

    // Supprimer une question par ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}