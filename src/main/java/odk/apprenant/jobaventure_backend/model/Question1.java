package odk.apprenant.jobaventure_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "question1")
public class Question1  {
    @Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

private Long interviewId; // ID de l'interview associée
private String emailEnfant; // Email de l'enfant
private String contenu; // Contenu de la question
private Date date; // Date de la question

// Getters et Setters
}