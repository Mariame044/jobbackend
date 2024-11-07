package odk.apprenant.jobaventure_backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.*;

@Entity
@Data
public class Enfant extends User {
    private int age;
    // Tentatives de réponses
    private int tentativesRestantes = 6;  // Initialement 3 tentatives
    private Date derniereTentative;        // Dernière tentative
    private boolean enAttente;

    @ElementCollection
    private Set<Long> questionsResolues = new HashSet<>(); // Ensemble des IDs des questions résolues
    // Nouveau champ pour stocker le score
    private int score = 0;  // Score initial à zéro
    // Indique si l'enfant est en attente
    @ManyToOne
    @JoinColumn(name = "parent_id")
    @JsonManagedReference
    private Parent parent; // Relation avec le parent (plusieurs enfants pour un parent)

    @ManyToMany
    @JoinTable(
            name = "enfant_badge",
            joinColumns = @JoinColumn(name = "enfant_id"),
            inverseJoinColumns = @JoinColumn(name = "badge_id")
    )
    private List<Badge> badge = new ArrayList<>();

    public void addBadge(Badge nomBadge) {
        // Check if badge already exists
        if (this.badge.stream().noneMatch(badge -> badge.getNom().equals(nomBadge.getNom()))) {
            Badge nouveauBadge = new Badge();
            nouveauBadge.setNom(nomBadge.getNom() + " - " + this.getNom()); // Associate the child's name with the badge
            this.badge.add(nouveauBadge); // Add the new badge to the list
        }
    }


    @ManyToMany
    @JoinTable(
            name = "enfant_Quiz",
            joinColumns = @JoinColumn(name = "enfant_id"),
            inverseJoinColumns = @JoinColumn(name = "quiz_id")
    )
    @JsonManagedReference
    private List<Quiz> quiz = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "enfant_video",
            joinColumns = @JoinColumn(name = "enfant_id"),
            inverseJoinColumns = @JoinColumn(name = "video_id")
    )
    @JsonManagedReference
    private List<Video> videosRegardees = new ArrayList<>();
    // Méthodes pour gérer les vidéos regardées
    public void addVideoRegardee(Video video) {
        if (!hasWatchedVideo(video)) {
            this.videosRegardees.add(video);
        }
    }

    public boolean hasWatchedVideo(Video video) {
        return this.videosRegardees.contains(video);
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "enfant_jeu",
            joinColumns = @JoinColumn(name = "enfant_id"),
            inverseJoinColumns = @JoinColumn(name = "jeu_id")
    )
    private List<Jeuderole> jeuderole = new ArrayList<>();


    @ManyToMany
    @JoinTable(
            name = "enfant_interview",
            joinColumns = @JoinColumn(name = "enfant_id"),
            inverseJoinColumns = @JoinColumn(name = "interview_id")
    )
    @JsonManagedReference
    private List<Interview> interviewregardees = new ArrayList<>();
    // Méthodes pour gérer les vidéos regardées
    public void addInterview(Interview interview) {
        if (!hasWatchedInterview(interview)) {
            this.interviewregardees.add(interview);
        }
    }

    public boolean hasWatchedInterview(Interview interview) {
        return this.interviewregardees.contains(interview);
    }

    // Constructeurs, getters, et setters


    public void addScore(int points) {
        if (points > 0) {
            this.score += points; // Ajoute les points au score actuel
        }
    }
    public Set<Long> getQuestionsResolues() {
        return questionsResolues;
    }

    public void addQuestionResolue(Long questionId) {
        this.questionsResolues.add(questionId);
    }

    public boolean hasResolvedQuestion(Long questionId) {
        return questionsResolues.contains(questionId); // Vérifie si la question est déjà résolue
    }
    // Méthode pour récupérer la progression complète de l'enfant
    public Map<String, Object> getProgression() {
        Map<String, Object> progression = new HashMap<>();
        progression.put("score", this.score);
        progression.put("questionsResolues", this.questionsResolues.size()); // Taille des questions résolues
        progression.put("badges", this.badge);
        progression.put("quiz", this.quiz);
        progression.put("videos", this.videosRegardees);
        progression.put("jeuxDeRole", this.jeuderole);
        progression.put("interviews", this.interviewregardees);

        return progression; // Retourne la progression complète
    }

    // Méthodes pour obtenir les listes
    public List<Video> getVideosRegardees() {
        return new ArrayList<>(this.videosRegardees); // Retourne une nouvelle liste des vidéos regardées
    }
    public List<Jeuderole> getJeux() {
        return new ArrayList<>(this.jeuderole); // Retourne une nouvelle liste des jeux
    }

    public List<Interview> getInterviews() {
        return new ArrayList<>(this.interviewregardees); // Retourne une nouvelle liste des interviews
    }

}
