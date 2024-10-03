package odk.apprenant.jobaventure_backend.service;


import odk.apprenant.jobaventure_backend.model.Jeuderole;
import odk.apprenant.jobaventure_backend.model.Question;
import odk.apprenant.jobaventure_backend.model.Quiz;
import odk.apprenant.jobaventure_backend.repository.JeuderoleRepository;
import odk.apprenant.jobaventure_backend.repository.QuestionRepository;
import odk.apprenant.jobaventure_backend.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private JeuderoleRepository jeuderoleRepository;

    // Récupérer toutes les questions
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    // Récupérer une question par son ID
    public Optional<Question> getQuestionById(Long id) {
        return questionRepository.findById(id);
    }

    // Créer une nouvelle question et l'associer à un quiz ou un jeu de rôle
    public Question createQuestion(Question question, Long quizId, Long jeuDeRoleId) {
        if (quizId != null) {
            Optional<Quiz> quiz = quizRepository.findById(quizId);
            if (quiz.isPresent()) {
                question.setQuiz(quiz.get());
            } else {
                throw new RuntimeException("Quiz non trouvé avec l'ID : " + quizId);
            }
        }

        if (jeuDeRoleId != null) {
            Optional<Jeuderole> jeuDeRole = jeuderoleRepository.findById(jeuDeRoleId);
            if (jeuDeRole.isPresent()) {
                question.setJeuderole(jeuDeRole.get());
            } else {
                throw new RuntimeException("Jeu de rôle non trouvé avec l'ID : " + jeuDeRoleId);
            }
        }

        return questionRepository.save(question);
    }

    // Mettre à jour une question existante
    public Question updateQuestion(Long id, Question questionDetails) {
        Optional<Question> optionalQuestion = questionRepository.findById(id);
        if (optionalQuestion.isPresent()) {
            Question question = optionalQuestion.get();
            question.setPoint(questionDetails.getPoint());
            question.setTexte(questionDetails.getTexte());
            question.setTypeQuestion(questionDetails.getTypeQuestion());
            question.setReponses(questionDetails.getReponses());

            // Mise à jour des relations quiz ou jeu de rôle
            if (questionDetails.getQuiz() != null) {
                question.setQuiz(questionDetails.getQuiz());
            }
            if (questionDetails.getJeuderole() != null) {
                question.setJeuderole(questionDetails.getJeuderole());
            }

            return questionRepository.save(question);
        } else {
            throw new RuntimeException("Question non trouvée avec l'ID : " + id);
        }
    }

    // Supprimer une question
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }
}
