package odk.apprenant.jobaventure_backend.controller;


import odk.apprenant.jobaventure_backend.model.Statistique;
import odk.apprenant.jobaventure_backend.service.StatistiqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistiques")
public class StatistiqueController {


    @Autowired
    private StatistiqueService statistiqueService;

    // Incrémente les vues d'un métier
    @PostMapping("/incrementer/vue-metier/{metierId}")
    public void incrementerVueMetier(@PathVariable Long metierId) {
        statistiqueService.incrementerVueMetier(metierId);
    }

    // Incrémente les vues d'une vidéo
    @PostMapping("/incrementer/vue-video/{videoId}")
    public void incrementerVueVideo(@PathVariable Long videoId) {
        statistiqueService.incrementerVueVideo(videoId);
    }

    // Incrémente les succès d'un quiz
    @PostMapping("/incrementer/succes-quiz/{quizId}")
    public void incrementerSuccesQuiz(@PathVariable Long quizId) {
        statistiqueService.incrementerSuccesQuiz(quizId);
    }

    // Récupère les métiers les plus explorés
    @GetMapping("/top-metiers")
    public List<Statistique> getTopMetiers() {
        return statistiqueService.getTopMetiers();
    }

    // Récupère les vidéos les plus vues
    @GetMapping("/top-videos")
    public List<Statistique> getTopVideos() {
        return statistiqueService.getTopVideos();
    }

    // Récupère les quiz les plus réussis
    @GetMapping("/top-quiz")
    public List<Statistique> getTopQuiz() {
        return statistiqueService.getTopQuiz();
    }
}