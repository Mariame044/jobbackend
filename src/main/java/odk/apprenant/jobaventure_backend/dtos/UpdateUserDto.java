package odk.apprenant.jobaventure_backend.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
@Data
public class UpdateUserDto {
    private MultipartFile image;
    private String email;
    private String fullName;
    private String password;

}
