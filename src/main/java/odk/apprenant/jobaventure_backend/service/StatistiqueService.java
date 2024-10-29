package odk.apprenant.jobaventure_backend.service;


import odk.apprenant.jobaventure_backend.model.Statistique;

import odk.apprenant.jobaventure_backend.repository.StatistiqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatistiqueService {
    @Autowired
    private StatistiqueRepository statistiqueRepository;

    // Incrémente le compteur de vues d’un métier
    public void incrementerVueMetier(Long metierId) {
        Statistique stats = statistiqueRepository.findByMetierId(metierId)
                .orElse(new Statistique(metierId, null, null,null, 0, 0, 0,0));
        stats.setVueMetier(stats.getVueMetier() + 1);
        statistiqueRepository.save(stats);
    }

    // Incrémente le compteur de vues d’une vidéo
    public void incrementerVueVideo(Long videoId) {
        Statistique stats = statistiqueRepository.findByVideoId(videoId)
                .orElse(new Statistique(null, videoId, null,null, 0, 0,0, 0));
        stats.setVueVideo(stats.getVueVideo() + 1);
        statistiqueRepository.save(stats);
    }

    // Incrémente le compteur de succès d’un quiz
    public void incrementerSuccesQuiz(Long quizId) {
        Statistique stats = statistiqueRepository.findByQuizId(quizId)
                .orElse(new Statistique(null, null,null, quizId, 0,0, 0, 0));
        stats.setSuccesQuiz(stats.getSuccesQuiz() + 1);
        statistiqueRepository.save(stats);
    }
    // Incrémente le compteur de succès d’un quiz
    public void incrementerVueInterview(Long interviewId) {
        Statistique stats = statistiqueRepository.findByQuizId(interviewId)
                .orElse(new Statistique(null, null,interviewId, null, 0,0, 0, 0));
        stats.setSuccesQuiz(stats.getSuccesQuiz() + 1);
        statistiqueRepository.save(stats);
    }

    // Obtenir les métiers les plus explorés
    public List<Statistique> getTopMetiers() {
        return statistiqueRepository.findAll().stream()
                .sorted((s1, s2) -> Integer.compare(s2.getVueMetier(), s1.getVueMetier()))
                .limit(10)
                .collect(Collectors.toList());
    }

    // Obtenir les vidéos les plus vues
    public List<Statistique> getTopVideos() {
        return statistiqueRepository.findAll().stream()
                .sorted((s1, s2) -> Integer.compare(s2.getVueVideo(), s1.getVueVideo()))
                .limit(10)
                .collect(Collectors.toList());
    }
    // Obtenir les vidéos les plus vues
    public List<Statistique> getTopInterviews() {
        return statistiqueRepository.findAll().stream()
                .sorted((s1, s2) -> Integer.compare(s2.getVueVideo(), s1.getVueVideo()))
                .limit(10)
                .collect(Collectors.toList());
    }

    // Obtenir les quiz les plus réussis
    public List<Statistique> getTopQuiz() {
        return statistiqueRepository.findAll().stream()
                .sorted((s1, s2) -> Integer.compare(s2.getSuccesQuiz(), s1.getSuccesQuiz()))
                .limit(10)
                .collect(Collectors.toList());
    }
}

