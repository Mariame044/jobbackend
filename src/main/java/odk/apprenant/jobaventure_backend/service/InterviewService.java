package odk.apprenant.jobaventure_backend.service;


import odk.apprenant.jobaventure_backend.model.Admin;
import odk.apprenant.jobaventure_backend.model.Interview;
import odk.apprenant.jobaventure_backend.repository.AdminRepository;
import odk.apprenant.jobaventure_backend.repository.InterviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    // Méthode pour obtenir l'administrateur connecté
    private Admin getCurrentAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Supposons que l'email est utilisé comme principal
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Administrateur non trouvé"));
    }

    public Interview ajouterInterview(Interview interview, MultipartFile fichier) throws IOException {

        if (interview.getMetier() == null) {
            throw new RuntimeException("La catégorie est obligatoire.");
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
}

