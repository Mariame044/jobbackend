package odk.apprenant.jobaventure_backend.exeption;

// Importation de la classe d'exception de base
public class MetierNotFoundException extends RuntimeException {
    public MetierNotFoundException(String message) {
        super(message); // Appelle le constructeur de la classe parent
    }

    public MetierNotFoundException(String message, Throwable cause) {
        super(message, cause); // Appelle le constructeur de la classe parent avec une cause
    }
}