package odk.apprenant.jobaventure_backend.dtos;

import lombok.Data;
import odk.apprenant.jobaventure_backend.model.Question;
@Data
public class QuestionDto {
    private Integer point;
    private String texte;
    private String typeQuestion;
    private Long jeuDeRoleId;
    private Long quizId;
    private Long reponseId;

    // Getters et Setters
}
