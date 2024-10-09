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
    public Optional<Reponse> getReponseById(long id) {
        return reponseRepository.findById(id);
    }

    // Créer une nouvelle réponse et l'associer à une ou plusieurs questions
    public Reponse createReponse(Reponse reponse) {
        return reponseRepository.save(reponse);
    }

    // Mettre à jour une réponse existante
    public Reponse updateReponse(int id, Reponse reponseDetails, List<Long> questionIds) {
        Optional<Reponse> optionalReponse = reponseRepository.findById((long) id);

        if (optionalReponse.isPresent()) {
            Reponse reponse = optionalReponse.get();
            reponse.setReponsepossible(reponseDetails.getReponsepossible());
            reponse.setCorrect(reponseDetails.getCorrect());

            // Mise à jour des relations avec les questions
            List<Question> questions = questionRepository.findAllById(questionIds);


            return reponseRepository.save(reponse);
        } else {
            throw new RuntimeException("Réponse non trouvée avec l'ID : " + id);
        }
    }

    // Supprimer une réponse
    public void deleteReponse(long id) {
        reponseRepository.deleteById(id);
    }
}