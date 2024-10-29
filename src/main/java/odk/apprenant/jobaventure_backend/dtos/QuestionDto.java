package odk.apprenant.jobaventure_backend.dtos;

import lombok.Data;
import odk.apprenant.jobaventure_backend.model.Question;
import odk.apprenant.jobaventure_backend.model.Trancheage;

@Data
public class QuestionDto {
    private Integer point;
    private String texte;
    private String typeQuestion;
    private Long jeuDeRoleId;
    private Long quizId;
    private Long reponseId;
    private Long trancheageId;

    // Getters et Setters
}
