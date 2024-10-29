package odk.apprenant.jobaventure_backend.dtos;

import lombok.Data;

@Data
public class QuizDto {
    private long id;
    private String titre;
    private String description;
    private Integer score;
    private Integer resultat;
    private Long badgeId;  // ID du badge associé
    private Long metierId; // ID du métier associé
    private Long trancheageId; // ID du métier associé
}