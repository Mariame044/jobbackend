package odk.apprenant.jobaventure_backend.dtos;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import odk.apprenant.jobaventure_backend.model.Role;
import odk.apprenant.jobaventure_backend.model.User;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReqRep {

    private int statusCode;
    private String error;
    private String message;
    private String token;
    private String refreshToken;
    private String expirationTime;
    private String nom;
    private String prenom;
    private String email;
    private String phone;
    private String password;
    private Role role;
    private User user;
    private List<User> userList;
}
