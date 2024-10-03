package odk.apprenant.jobaventure_backend.service;


import odk.apprenant.jobaventure_backend.model.Question;
import odk.apprenant.jobaventure_backend.model.Reponse;
import odk.apprenant.jobaventure_backend.repository.QuestionRepository;
import odk.apprenant.jobaventure_backend.repository.ReponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReponseService {

    @Autowired
    private ReponseRepository reponseRepository;

    @Autowired
    private QuestionRepository questionRepository;

    // Récupérer toutes les réponses
    public List<Reponse> getAllReponses() {
        return reponseRepository.findAll();
    }

    // Récupérer une réponse par son ID
    public Optional<Reponse> getReponseById(int id) {
        return reponseRepository.findById(id);
    }

    // Créer une nouvelle réponse et l'associer à une question
    public Reponse createReponse(Reponse reponse, Long questionId) {
        Optional<Question> question = questionRepository.findById(questionId);
        if (question.isPresent()) {
            reponse.setQuestion(question.get());
            return reponseRepository.save(reponse);
        } else {
            throw new RuntimeException("Question non trouvée avec l'ID : " + questionId);
        }
    }

    // Mettre à jour une réponse existante
    public Reponse updateReponse(int id, Reponse reponseDetails) {
        Optional<Reponse> optionalReponse = reponseRepository.findById(id);
        if (optionalReponse.isPresent()) {
            Reponse reponse = optionalReponse.get();
            reponse.setLibelle(reponseDetails.getLibelle());
            reponse.setCorrect(reponseDetails.getCorrect());

            // Mise à jour de la relation avec la question, si nécessaire
            if (reponseDetails.getQuestion() != null) {
                reponse.setQuestion(reponseDetails.getQuestion());
            }

            return reponseRepository.save(reponse);
        } else {
            throw new RuntimeException("Réponse non trouvée avec l'ID : " + id);
        }
    }

    // Supprimer une réponse
    public void deleteReponse(int id) {
        reponseRepository.deleteById(id);
    }
}