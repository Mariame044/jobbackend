package odk.apprenant.jobaventure_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data

public class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String description;

    @OneToMany(mappedBy = "badge")
    private List<Quiz> quizzes;  // Un badge est lié à plusieurs quiz

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin; // Int

    @ManyToMany(mappedBy = "badge")
    private List<Enfant> enfant = new ArrayList<>();


}
