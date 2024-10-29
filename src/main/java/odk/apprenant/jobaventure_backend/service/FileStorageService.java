package odk.apprenant.jobaventure_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    // Dossier où les vidéos et les images seront enregistrées
    private final String dossierVidéos = "uploads/videos/";
    private final String dossierImages = "uploads/images/";
    private final String dossierAudios = "uploads/audios/";  // Nouveau dossier pour les audios

    // Extensions autorisées pour les fichiers
    private final String[] extensionsImages = { "jpg", "jpeg", "png", "gif" };
    private final String[] extensionsVidéos = { "mp4", "avi", "mov", "wmv" };
    private final String[] extensionsAudios = { "mp3", "wav", "aac", "flac" };  // Nouvelles extensions pour audio

    public String sauvegarderVideo(MultipartFile fichier) throws IOException {
        // Vérifie si le fichier est une vidéo
        if (!estExtensionValide(fichier.getOriginalFilename(), extensionsVidéos)) {
            throw new IOException("Le fichier n'est pas une vidéo valide.");
        }
        return sauvegarderFichier(fichier, dossierVidéos);
    }

    public String sauvegarderImage(MultipartFile fichier) throws IOException {
        // Vérifie si le fichier est une image
        if (!estExtensionValide(fichier.getOriginalFilename(), extensionsImages)) {
            throw new IOException("Le fichier n'est pas une image valide.");
        }
        return sauvegarderFichier(fichier, dossierImages);
    }
    public String sauvegarderAudio(MultipartFile fichier) throws IOException {
        // Vérifie si le fichier est un audio
        if (!estExtensionValide(fichier.getOriginalFilename(), extensionsAudios)) {
            throw new IOException("Le fichier n'est pas un fichier audio valide.");
        }
        return sauvegarderFichier(fichier, dossierAudios);  // Utilise le nouveau dossier audio
    }


    private String sauvegarderFichier(MultipartFile fichier, String dossier) throws IOException {
        // Vérifie si le dossier de destination existe, sinon le créer
        Path cheminDossier = Paths.get(dossier);
        if (!Files.exists(cheminDossier)) {
            Files.createDirectories(cheminDossier);
        }

        // Vérifie si le nom du fichier est valide
        String nomFichier = fichier.getOriginalFilename();
        if (nomFichier == null || nomFichier.isEmpty()) {
            throw new IOException("Le nom du fichier est invalide.");
        }

        // Génère un nom de fichier unique pour éviter les conflits
        String nomFichierUnique = UUID.randomUUID().toString() + "_" + nomFichier;
        Path cheminFichier = cheminDossier.resolve(nomFichierUnique);

        // Sauvegarde le fichier dans le dossier
        Files.copy(fichier.getInputStream(), cheminFichier);

        // Retourne le chemin relatif où le fichier est stocké
        return cheminFichier.toString(); // Tu peux modifier cela pour renvoyer un chemin relatif si besoin
    }

    private boolean estExtensionValide(String nomFichier, String[] extensionsAutorisees) {
        if (nomFichier == null) {
            return false;
        }
        String extension = nomFichier.substring(nomFichier.lastIndexOf('.') + 1).toLowerCase();
        for (String ext : extensionsAutorisees) {
            if (ext.equals(extension)) {
                return true;
            }
        }
        return false;
    }
}
