package odk.apprenant.jobaventure_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Statistique {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long metierId;  // ID du métier
    private Long videoId;   // ID de la vidéo
    private Long interviewId;   // ID de la vidéo
    private Long quizId;    // ID du quiz

    private int vueMetier;  // Nombre de vues pour le métier
    private int vueVideo;   // Nombre de vues pour la vidéo
    private int vueInterview;
    private int succesQuiz; // Nombre de succès pour le quiz


    // Constructeur sans paramètres
    public Statistique() {}

    // Constructeur avec tous les paramètres nécessaires
    public Statistique(Long metierId, Long videoId,Long interviewId, Long quizId, int vueMetier, int vueVideo,int vueInterview, int succesQuiz) {
        this.metierId = metierId;
        this.videoId = videoId;
        this.interviewId = interviewId;
        this.quizId = quizId;
        this.vueMetier = vueMetier;
        this.vueVideo = vueVideo;
        this.vueInterview = vueInterview;
        this.succesQuiz = succesQuiz;
    }
}