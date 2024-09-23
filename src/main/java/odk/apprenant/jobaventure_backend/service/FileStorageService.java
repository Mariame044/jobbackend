package odk.apprenant.jobaventure_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {

    // Dossier où les vidéos seront enregistrées
    private final String dossierVidéos = "uploads/videos/";
    private final String dossierImages = "uploads/images/";


    public String sauvegarderVideo(MultipartFile fichier) throws IOException {
        return sauvegarderFichier(fichier, dossierVidéos);
    }

    public String sauvegarderImage(MultipartFile fichier) throws IOException {
        return sauvegarderFichier(fichier, dossierImages);
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
        String nomFichierUnique = System.currentTimeMillis() + "_" + nomFichier;
        Path cheminFichier = cheminDossier.resolve(nomFichierUnique);

        // Sauvegarde le fichier dans le dossier
        Files.copy(fichier.getInputStream(), cheminFichier);

        // Retourne le chemin où le fichier est stocké
        return cheminFichier.toString();
    }
}