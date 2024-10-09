package odk.apprenant.jobaventure_backend.service;


import jakarta.persistence.EntityNotFoundException;
import odk.apprenant.jobaventure_backend.dtos.QuestionDto;
import odk.apprenant.jobaventure_backend.model.*;
import odk.apprenant.jobaventure_backend.repository.JeuderoleRepository;
import odk.apprenant.jobaventure_backend.repository.QuestionRepository;
import odk.apprenant.jobaventure_backend.repository.QuizRepository;
import odk.apprenant.jobaventure_backend.repository.ReponseRepository;
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
    @Autowired
    private ReponseRepository reponseRepository;

    // Récupérer toutes les questions
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    // Récupérer une question par son ID
    public Optional<Question> getQuestionById(Long id) {
        return questionRepository.findById(id);
    }

    public Question createQuestion(Question question, Long quizId, Long jeuDeRoleId, Long reponseId) {
        // Vérifiez que le type de question est défini
        if (question.getTypeQuestion() == null) {
            throw new RuntimeException("Le type de question est obligatoire.");
        }

        // Vérification et association d'un jeu de rôle si le type de question est JEUX
        if (question.getTypeQuestion() == TypeQuestion.JEU_DE_ROLE) {
            if (jeuDeRoleId != null) {
                Optional<Jeuderole> jeuDeRole = jeuderoleRepository.findById(jeuDeRoleId);
                if (jeuDeRole.isPresent()) {
                    question.setJeuderole(jeuDeRole.get());
                } else {
                    throw new RuntimeException("Jeu de rôle non trouvé avec l'ID : " + jeuDeRoleId);
                }
            } else {
                throw new RuntimeException("L'ID du jeu de rôle est obligatoire pour le type de question 'JEU_DE_ROLE'.");
            }
        } else if (question.getTypeQuestion() == TypeQuestion. Quiz) { // Si le type de question est QUIZ
            if (quizId != null) {
                Optional<Quiz> quiz = quizRepository.findById(quizId);
                if (quiz.isPresent()) {
                    question.setQuiz(quiz.get());
                } else {
                    throw new RuntimeException("Quiz non trouvé avec l'ID : " + quizId);
                }
            } else {
                throw new RuntimeException("L'ID du quiz est obligatoire pour le type de question 'QUIZ'.");
            }
        } else {
            throw new RuntimeException("Type de question inconnu : " + question.getTypeQuestion());
        }

        // Vérification et association d'une réponse
        if (reponseId != null) {
            Optional<Reponse> reponse = reponseRepository.findById(reponseId);
            if (reponse.isPresent()) {
                question.setReponse(reponse.get());
            } else {
                throw new RuntimeException("Réponse non trouvée avec l'ID : " + reponseId);
            }
        }

        return questionRepository.save(question);
    }

    // Mettre à jour une question existante
    public Question updateQuestion(Long id, Question questionDetails, Long quizId, Long jeuDeRoleId, Long reponseId) {
        Optional<Question> optionalQuestion = questionRepository.findById(id);
        if (optionalQuestion.isPresent()) {
            Question question = optionalQuestion.get();
            question.setPoint(questionDetails.getPoint());
            question.setTexte(questionDetails.getTexte());
            question.setTypeQuestion(questionDetails.getTypeQuestion());

            // Mise à jour des relations quiz, jeu de rôle et réponse
            if (quizId != null) {
                Optional<Quiz> quiz = quizRepository.findById(quizId);
                quiz.ifPresent(question::setQuiz);
            }
            if (jeuDeRoleId != null) {
                Optional<Jeuderole> jeuDeRole = jeuderoleRepository.findById(jeuDeRoleId);
                jeuDeRole.ifPresent(question::setJeuderole);
            }
            if (reponseId != null) {
                Optional<Reponse> reponse = reponseRepository.findById(reponseId);
                reponse.ifPresent(question::setReponse);
            }

            return questionRepository.save(question);
        } else {
            throw new RuntimeException("Question non trouvée avec l'ID : " + id);
        }
    }

    // Supprimer une question par son ID
    public void deleteQuestion(Long id) {
        if (questionRepository.existsById(id)) {
            questionRepository.deleteById(id);
        } else {
            throw new RuntimeException("La question avec l'ID " + id + " n'existe pas.");
        }
    }
}