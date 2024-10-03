package odk.apprenant.jobaventure_backend.dtos;

public class LoginResponseDto {
    private String token;
    private String message;

    public LoginResponseDto(String token, String message) {
        this.token = token;
        this.message = message;
    }

    // Getters et setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}