package odk.apprenant.jobaventure_backend.service;


import odk.apprenant.jobaventure_backend.model.Badge;
import odk.apprenant.jobaventure_backend.model.Metier;
import odk.apprenant.jobaventure_backend.model.Quiz;
import odk.apprenant.jobaventure_backend.repository.BadgeRepository;
import odk.apprenant.jobaventure_backend.repository.MetierRepository;
import odk.apprenant.jobaventure_backend.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private MetierRepository metierRepository;

    // Récupérer tous les quizzes
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    // Récupérer un quiz par son ID
    public Optional<Quiz> getQuizById(Long id) {
        return quizRepository.findById(id);
    }

    // Créer un nouveau quiz et l'associer à un métier et un badge
    public Quiz createQuiz(Quiz quiz, Long badgeId, Long metierId) {
        // Associer le quiz à un badge si badgeId est spécifié
        if (badgeId != null) {
            Optional<Badge> badge = badgeRepository.findById(badgeId);
            if (badge.isPresent()) {
                quiz.setBadge(badge.get());
            } else {
                throw new RuntimeException("Badge non trouvé avec l'ID : " + badgeId);
            }
        }

        // Associer le quiz à un métier si metierId est spécifié
        if (metierId != null) {
            Optional<Metier> metier = metierRepository.findById(metierId);
            if (metier.isPresent()) {
                quiz.setMetier(metier.get());
            } else {
                throw new RuntimeException("Métier non trouvé avec l'ID : " + metierId);
            }
        }

        return quizRepository.save(quiz);
    }

    // Mettre à jour un quiz existant
    public Quiz updateQuiz(Long id, Quiz quizDetails) {
        Optional<Quiz> optionalQuiz = quizRepository.findById(id);
        if (optionalQuiz.isPresent()) {
            Quiz quiz = optionalQuiz.get();
            quiz.setTitre(quizDetails.getTitre());
            quiz.setDescription(quizDetails.getDescription());
            quiz.setScore(quizDetails.getScore());
            quiz.setResultat(quizDetails.getResultat());
            quiz.setBadget(quizDetails.getBadget());

            // Mise à jour de la relation avec le badge
            if (quizDetails.getBadge() != null) {
                quiz.setBadge(quizDetails.getBadge());
            }

            // Mise à jour de la relation avec le métier
            if (quizDetails.getMetier() != null) {
                quiz.setMetier(quizDetails.getMetier());
            }

            return quizRepository.save(quiz);
        } else {
            throw new RuntimeException("Quiz non trouvé avec l'ID : " + id);
        }
    }

    // Supprimer un quiz
    public void deleteQuiz(Long id) {
        quizRepository.deleteById(id);
    }
}