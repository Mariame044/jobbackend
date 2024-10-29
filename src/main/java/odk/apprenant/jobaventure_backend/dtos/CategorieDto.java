package odk.apprenant.jobaventure_backend.dtos;

import lombok.Data;
import odk.apprenant.jobaventure_backend.model.Admin;
import odk.apprenant.jobaventure_backend.model.FileInfo;

import java.util.ArrayList;

@Data
public class CategorieDto {
    private Long id;
    private String nom;
    private Admin admin;

}
