package odk.apprenant.jobaventure_backend.service;


import odk.apprenant.jobaventure_backend.model.Admin;
import odk.apprenant.jobaventure_backend.model.Enfant;
import odk.apprenant.jobaventure_backend.model.Interview;
import odk.apprenant.jobaventure_backend.model.Video;
import odk.apprenant.jobaventure_backend.repository.AdminRepository;
import odk.apprenant.jobaventure_backend.repository.EnfanrRepository;
import odk.apprenant.jobaventure_backend.repository.InterviewRepository;
import odk.apprenant.jobaventure_backend.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InterviewService {
    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private EnfanrRepository enfanrRepository;
    @Autowired
    private StatistiqueService statistiqueService;


    // Méthode pour obtenir l'administrateur connecté
    private Admin getCurrentAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Supposons que l'email est utilisé comme principal
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Administrateur non trouvé"));
    }
    private Enfant getCurrentEnfant() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Supposons que l'email est utilisé comme principal

        // Rechercher l'enfant par son email
        return enfanrRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("L'enfant avec l'email " + email + " n'a pas été trouvé"));
    }
    public Interview ajouterInterview(Interview interview, MultipartFile fichier) throws IOException {

        if (interview.getMetier() == null) {
            throw new RuntimeException("La catégorie est obligatoire.");
        }
        if (interview.getTrancheage() == null) {
            throw new RuntimeException("La tranche age est obligatoire.");
        }
        // Sauvegarde le fichier vidéo
        String cheminVideo = fileStorageService.sauvegarderVideo(fichier);
        interview.setUrl(cheminVideo); // Définit l'URL ou le chemin de la vidéo
        Admin admin = getCurrentAdmin();
        interview.setAdmin(admin); // Enregi
        return interviewRepository.save(interview);
    }


    public List<Interview> trouverToutesLesInterview() {
        return interviewRepository.findAll();
    }


    public Optional<Interview> trouverInterviewParId(Long id) {
        return interviewRepository.findById(id);
    }

    public List<Interview> getInterviewsByMetierId(Long metierId) {
        return interviewRepository.findByMetierId(metierId);
    }

    public Interview modifierInterview(Long id, Interview interview) {
        Optional<Interview> interviewExistante = interviewRepository.findById(id);
        if (interviewExistante.isPresent()) {
            Interview i = interviewExistante.get();
            i.setDuree(interview.getDuree());
            i.setDescription(interview.getDescription());
            i.setAdmin(interview.getAdmin());
            i.setMetier(interview.getMetier());
            return interviewRepository.save(i);
        } else {
            return null;
        }
    }


    public void supprimerInterview(Long id) {
        interviewRepository.deleteById(id);
    }


    public List<Interview> trouverVideosPourEnfantParAge() {
        Enfant enfant = getCurrentEnfant(); // Récupère l'enfant connecté
        int ageEnfant = enfant.getAge();    // Récupère l'âge de l'enfant

        // Récupère toutes les vidéos
        List<Interview> toutesLesInterview = interviewRepository.findAll();
        List<Interview> interviewsFiltrees = new ArrayList<>();

        // Filtrer les vidéos par tranche d'âge
        for (Interview interview : toutesLesInterview) {
            if (interview.getTrancheage() != null) {
                int ageMin = interview.getTrancheage().getAgeMin();
                int ageMax = interview.getTrancheage().getAgeMax();
                if (ageEnfant >= ageMin && ageEnfant <= ageMax) {
                    interviewsFiltrees.add(interview);
                }
            }
        }
        return interviewsFiltrees; // Retourne les vidéos filtrées
    }
    public List<Interview> trouverInterviewParMetierEtAge(Long metierId) {
        // Récupérer l'enfant connecté
        Enfant enfant = getCurrentEnfant();
        int ageEnfant = enfant.getAge();

        // Récupérer toutes les vidéos par métier
        List<Interview> interviewsParMetier = interviewRepository.findByMetierId(metierId);
        List<Interview> interviewsFiltrees = new ArrayList<>();

        // Filtrer les vidéos en fonction de la tranche d'âge de l'enfant
        for (Interview interview : interviewsParMetier) {
            if (interview.getTrancheage() != null) {
                int ageMin = interview.getTrancheage().getAgeMin();
                int ageMax = interview.getTrancheage().getAgeMax();
                if (ageEnfant >= ageMin && ageEnfant <= ageMax) {
                    interviewsFiltrees.add(interview);
                }
            }
        }

        return interviewsFiltrees; // Retourne les vidéos filtrées par métier et tranche d'âge
    }

    public Interview interviewregardees(Long id) {
        Optional<Interview> interviewOptional = interviewRepository.findById(id);
        if (interviewOptional.isPresent()) {
            Interview interview = interviewOptional.get();

            // Incrémentez le nombre de vues
            interview.setNombreDeVues(interview.getNombreDeVues() + 1);

            // Enregistrez les modifications de la vidéo
            interviewRepository.save(interview);

            // Obtenez l'enfant connecté
            Enfant enfant = getCurrentEnfant(); // Méthode pour obtenir l'enfant connecté

            // Vérifiez si la vidéo a déjà été regardée
            if (!enfant.hasWatchedInterview(interview)) {
                // Si la vidéo n'a pas été regardée, ajoutez-la à l'enfant
                enfant.addInterview(interview); // Ajoutez la vidéo à l'enfant

                // Enregistrez les modifications de l'enfant
                enfanrRepository.save(enfant);
            } else {
                // Optionnel : gérer le cas où la vidéo a déjà été regardée
                System.out.println("Cette interview a déjà été regardée par l'enfant.");
            }

            return interview; // Retourne la vidéo pour visionnage
        } else {
            throw new RuntimeException("Interview non trouvée avec l'ID : " + id);
        }
    }

}

