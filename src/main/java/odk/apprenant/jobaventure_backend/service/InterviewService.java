package odk.apprenant.jobaventure_backend.service;


import odk.apprenant.jobaventure_backend.model.*;
import odk.apprenant.jobaventure_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private Question1Repository question1Repository; // Injectez le repository
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


    public void poserQuestion(Long interviewId, String contenu) {
        Enfant enfant = getCurrentEnfant(); // Récupérer l'enfant connecté
        String emailEnfant = enfant.getEmail(); // Supposons que vous ayez un getter pour l'email

        // Créer une nouvelle question
        Question1 question = new Question1();
        question.setInterviewId(interviewId);
        question.setEmailEnfant(emailEnfant);
        question.setContenu(contenu);
        question.setDate(new Date()); // Date actuelle

        // Enregistrez la question dans la base de données
        question1Repository.save(question);

        // Appel de la méthode pour envoyer l'email au formateur
        envoyerEmailAuFormateur(interviewId, contenu, emailEnfant);
    }

    private void envoyerEmailAuFormateur(Long interviewId, String contenu, String emailEnfant) {
        // Récupérer l'interview pour obtenir le professionnel (formateur) associé
        Interview interview = trouverInterviewParId(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview non trouvée avec l'ID : " + interviewId));

        // Assurez-vous que l'interview a un professionnel associé
        if (interview.getAdmin() == null) {
            throw new RuntimeException("Aucun professionnel associé à cette interview.");
        }

        // Préparer le message
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(interview.getAdmin().getEmail()); // L'email du formateur
        message.setSubject("Nouvelle question d'un enfant concernant l'interview");
        message.setText("Bonjour,\n\nUn enfant a posé une nouvelle question concernant votre interview.\n\n" +
                "Détails de la question : " + contenu + "\n" +
                "Posée par l'enfant avec l'email : " + emailEnfant + "\n" +
                "ID de l'interview : " + interview.getDescription() + "\n\n" +
                "Merci de votre attention.");

        // Log the email details for debugging
        System.out.println("Envoi d'email à : " + interview.getAdmin().getEmail());
        System.out.println("Sujet : " + message.getSubject());
        System.out.println("Corps du message : " + message.getText());

        // Envoyer l'email
        try {
            mailSender.send(message);
            System.out.println("Email envoyé avec succès à " + interview.getAdmin().getEmail());
        } catch (Exception e) {
            // Gérer l'erreur d'envoi d'e-mail ici
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
    }

}

