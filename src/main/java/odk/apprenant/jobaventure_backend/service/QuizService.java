package odk.apprenant.jobaventure_backend.service;


import odk.apprenant.jobaventure_backend.dtos.QuizDto;
import odk.apprenant.jobaventure_backend.model.*;
import odk.apprenant.jobaventure_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private EnfanrRepository enfanrRepository;


    @Autowired
    private MetierRepository metierRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private TrancheageRepository trancheageRepository;

    private Enfant getCurrentEnfant() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Supposons que l'email est utilisé comme principal

        // Rechercher l'enfant par son email
        return enfanrRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("L'enfant avec l'email " + email + " n'a pas été trouvé"));
    }

    // Récupérer tous les quizzes et les convertir en QuizDTO
    public List<QuizDto> getAllQuizzes() {
        List<Quiz> quizzes = quizRepository.findAll();
        return quizzes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    // Méthode de conversion de Quiz en QuizDto
    private QuizDto convertToDTO(Quiz quiz) {
        QuizDto dto = new QuizDto();
        dto.setId(quiz.getId());
        dto.setTitre(quiz.getTitre());
        dto.setDescription(quiz.getDescription());
        dto.setScore(quiz.getScore());
        dto.setResultat(quiz.getResultat());

        // Récupérer l'ID du métier associé
        if (quiz.getMetier() != null) {
            dto.setMetierId(quiz.getMetier().getId());
        } else {
            throw new RuntimeException("Le quiz n'est pas associé à un métier"); // Gestion d'erreur
        }

        return dto;
    }

    // Récupérer un quiz par son ID
    public Optional<Quiz> getQuizById(Long quizId) {
        return quizRepository.findById(quizId);
    }

    // Méthode de création de Quiz avec vérification de metierId
    public Quiz createQuiz(QuizDto quizDto) {
        // Vérifiez que le metierId est présent
        if (quizDto.getMetierId() == null) {
            throw new IllegalArgumentException("Le champ metierId est obligatoire");
        }
        if (quizDto.getTrancheageId() == null) {
            throw new IllegalArgumentException("Le champ metierId est obligatoire");
        }

        // Récupérer le Metier correspondant
        Optional<Metier> metier = metierRepository.findById(quizDto.getMetierId());
        if (!metier.isPresent()) {
            throw new RuntimeException("Métier non trouvé avec l'ID : " + quizDto.getMetierId());
        }
        // Récupérer le Metier correspondant
        Optional<Trancheage> trancheage= trancheageRepository.findById(quizDto.getTrancheageId());
        if (!trancheage.isPresent()) {
            throw new RuntimeException("Tranche age non trouvé avec l'ID : " + quizDto.getTrancheageId());
        }

        // Créer le Quiz et l'associer au Metier
        Quiz quiz = new Quiz();
        quiz.setTitre(quizDto.getTitre());
        quiz.setDescription(quizDto.getDescription());
        quiz.setScore(quizDto.getScore());
        quiz.setResultat(quizDto.getResultat());
        quiz.setMetier(metier.get()); // Associer le Metier
        quiz.setTrancheage(trancheage.get()); // Associer le Metier

        // Vous pouvez ajouter une logique pour le badge ici si nécessaire

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

    // Jouer un quiz en récupérant les questions associées
    public List<Question> jouer(Long quizId) {
        // Récupérer le quiz par son ID
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz avec l'ID " + quizId + " non trouvé."));

        // Récupérer les questions liées à ce quiz
        List<Question> questions = questionRepository.findByquizId(quizId);

        // Vérifiez si des questions sont disponibles
        if (questions.isEmpty()) {
            throw new RuntimeException("Aucune question disponible pour le quiz avec l'ID " + quizId);
        }

        return questions; // Retourner la liste des questions
    }

    public String verifierReponse(Long quizId, Long questionId, String reponseDonnee) {
        Enfant enfant = getCurrentEnfant(); // Récupérer l'enfant connecté

        // Vérifier si l'enfant est en attente
        if (enfant.isEnAttente()) {
            long tempsRestant = 4 * 60 * 60 * 1000 - (new Date().getTime() - enfant.getDerniereTentative().getTime());
            if (tempsRestant > 0) {
                long heuresRestantes = tempsRestant / (1000 * 60 * 60);
                return "Vous devez attendre " + heuresRestantes + " heures avant de pouvoir jouer à nouveau.";
            } else {
                enfant.setEnAttente(false); // L'enfant peut jouer à nouveau
                enfant.setTentativesRestantes(6); // Réinitialiser les tentatives
                enfanrRepository.save(enfant); // Sauvegarder l'état de l'enfant
            }
        }

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz avec l'ID " + quizId + " non trouvé."));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("La question avec l'ID " + questionId + " n'existe pas."));

        // Vérifier si l'enfant a déjà répondu correctement à cette question
        if (enfant.hasResolvedQuestion(questionId)) {
            return "Vous avez déjà répondu correctement à cette question. Aucun point ne sera attribué.";
        }

        // Vérifier la validité de la réponse donnée (est-elle dans les réponses possibles ?)
        if (question.getReponse() != null && question.getReponse().getReponsepossible().contains(reponseDonnee)) {
            if (question.getReponse().getCorrect().equals(reponseDonnee)) {
                int pointsGagnes = question.getPoint();
                enfant.addScore(pointsGagnes); // Ajouter les points au score de l'enfant
                enfant.addQuestionResolue(questionId); // Marquer la question comme résolue
                enfant.setTentativesRestantes(6); // Réinitialiser les tentatives après une bonne réponse
                enfant.setDerniereTentative(new Date()); // Mettre à jour la dernière tentative
                enfanrRepository.save(enfant); // Sauvegarder les changements

                return "Réponse correcte! Vous avez gagné " + pointsGagnes + " points. Score total : " + enfant.getScore();
            } else {
                enfant.setTentativesRestantes(enfant.getTentativesRestantes() - 1);
                enfant.setDerniereTentative(new Date()); // Mettre à jour la dernière tentative
                enfanrRepository.save(enfant); // Sauvegarder les tentatives restantes mises à jour

                if (enfant.getTentativesRestantes() <= 0) {
                    enfant.setEnAttente(true); // Passer en mode attente
                    enfant.setDerniereTentative(new Date()); // Mettre la date actuelle de la tentative échouée
                    enfanrRepository.save(enfant); // Sauvegarder avec le statut en attente
                    return "Tentatives épuisées! Vous devez attendre 4 heures avant de pouvoir jouer à nouveau.";
                }
                return "Réponse incorrecte. Tentatives restantes : " + enfant.getTentativesRestantes();
            }
        }
        return "La réponse donnée n'est pas valide.";
    }

    public int calculerScore(Long quizId, Map<Long, String> reponsesDonnees) {
        Enfant enfant = getCurrentEnfant(); // Récupérer l'enfant connecté
        int scoreTotal = 0;
        int bonnesReponses = 0;

        // Parcourir toutes les réponses données par l'enfant pour ce jeu
        for (Map.Entry<Long, String> entry : reponsesDonnees.entrySet()) {
            Long questionId = entry.getKey();
            String reponseDonnee = entry.getValue();

            // Vérifier si la réponse donnée est correcte
            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new RuntimeException("Question non trouvée"));

            // Si la réponse est correcte, on incrémente le score
            if (question.getReponse().getCorrect().equals(reponseDonnee)) {
                scoreTotal += question.getPoint(); // Ajouter les points de la question
                bonnesReponses++;
            }
        }

        // Calcul du pourcentage de bonnes réponses

        double pourcentage = ((double) bonnesReponses / reponsesDonnees.size()) * 100;


        // Mise à jour du score total de l'enfant
        enfant.setScore(enfant.getScore() + scoreTotal); // Ajouter le score du jeu au score total

        // Sauvegarder l'enfant avec le nouveau score et les badges
        enfanrRepository.save(enfant);

        return scoreTotal; // Retourner le score total obtenu dans ce jeu
    }
    public List<Quiz> trouverQuizParMetierEtAge(Long metierId) {
        // Récupérer l'enfant connecté
        Enfant enfant = getCurrentEnfant();
        int ageEnfant = enfant.getAge();

        // Récupérer toutes les vidéos par métier
        List<Quiz> quizParMetier = quizRepository.findByMetierId(metierId);
        List<Quiz> quizFiltrees = new ArrayList<>();

        // Filtrer les vidéos en fonction de la tranche d'âge de l'enfant
        for (Quiz quiz : quizParMetier) {
            if ( quiz.getTrancheage() != null) {
                int ageMin =  quiz.getTrancheage().getAgeMin();
                int ageMax =  quiz.getTrancheage().getAgeMax();
                if (ageEnfant >= ageMin && ageEnfant <= ageMax) {
                    quizFiltrees.add(quiz);
                }
            }
        }

        return quizFiltrees; // Retourne les vidéos filtrées par métier et tranche d'âge
    }
    public List<Quiz> trouverQuizPourEnfantParAge() {
        Enfant enfant = getCurrentEnfant(); // Récupère l'enfant connecté
        int ageEnfant = enfant.getAge();    // Récupère l'âge de l'enfant

        // Récupère toutes les vidéos
        List<Quiz> toutesLesquiz = quizRepository.findAll();
        List<Quiz> quizFiltrees = new ArrayList<>();

        // Filtrer les vidéos par tranche d'âge
        for (Quiz quiz : toutesLesquiz) {
            if (quiz.getTrancheage() != null) {
                int ageMin = quiz.getTrancheage().getAgeMin();
                int ageMax = quiz.getTrancheage().getAgeMax();
                if (ageEnfant >= ageMin && ageEnfant <= ageMax) {
                    quizFiltrees.add(quiz);
                }
            }
        }
        return quizFiltrees; // Retourne les vidéos filtrées
    }

}
