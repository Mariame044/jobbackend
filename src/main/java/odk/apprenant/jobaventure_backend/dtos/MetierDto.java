package odk.apprenant.jobaventure_backend.dtos;

import lombok.Data;
import odk.apprenant.jobaventure_backend.model.Categorie;

import java.util.List;

// MetierDTO.java
@Data
public class MetierDto {
    private Long id;
    private String nom;
    private String description;
    private String imageUrl;

    private CategorieDto categorie;
    private List<Long> quizIds; // Supposons que vous souhaitez seulement stocker des IDs pour les quiz
    private List<Long> videoIds; // Même principe pour les vidéos
    private List<Long> interviewIds; // Pour les interviews
    private List<Long> jeuderoleIds; // Pour les jeux de rôle


}


