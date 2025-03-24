package site.easy.to.build.crm.entity.temp;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entité temporaire pour l'importation CSV des données client
 * Cette classe n'est pas une entité JPA mais un simple POJO pour stocker les données CSV
 */
public class TempCustomer {

    // Champs correspondant à l'entité Customer
    private String name;
    private String email;
    private String position;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String country;
    private String description;
    private String twitter;
    private String facebook;
    private String youtube;
    
    // Champs pour établir les relations
    private String userEmail; // Pour lier au User
    private String loginUsername; // Pour CustomerLoginInfo
    private String loginPassword; // Pour CustomerLoginInfo
    
    // Champs financiers
    private String budget;
    
    // Champs pour la validation et le suivi
    private int lineNumber; // Ligne dans le fichier CSV
    private boolean valid = true;
    private String errorMessage;
    
    // Constructeur par défaut
    public TempCustomer() {
    }
    
    // Constructeur avec tous les champs
    public TempCustomer(String name, String email, String position, String phone, 
                        String address, String city, String state, String country,
                        String description, String twitter, String facebook, 
                        String youtube, String userEmail, String loginUsername, 
                        String loginPassword, String budget, int lineNumber) {
        this.name = name;
        this.email = email;
        this.position = position;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.description = description;
        this.twitter = twitter;
        this.facebook = facebook;
        this.youtube = youtube;
        this.userEmail = userEmail;
        this.loginUsername = loginUsername;
        this.loginPassword = loginPassword;
        this.budget = budget;
        this.lineNumber = lineNumber;
    }
    
    // Méthode pour valider les données
    public boolean validate() {
        if (name == null || name.trim().isEmpty()) {
            valid = false;
            errorMessage = "Name is required";
            return false;
        }
        
        if (email == null || email.trim().isEmpty()) {
            valid = false;
            errorMessage = "Email is required";
            return false;
        }
        
        // Validation simple du format email
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            valid = false;
            errorMessage = "Invalid email format";
            return false;
        }
        
        if (country == null || country.trim().isEmpty()) {
            valid = false;
            errorMessage = "Country is required";
            return false;
        }
        
        // Validation du budget (si présent)
        if (budget != null && !budget.trim().isEmpty()) {
            try {
                new BigDecimal(budget);
            } catch (NumberFormatException e) {
                valid = false;
                errorMessage = "Budget must be a valid number";
                return false;
            }
        }
        
        return true;
    }
    
    // Getters et setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    // Autres getters et setters...
    
    public String getUserEmail() {
        return userEmail;
    }
    
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    
    public String getLoginUsername() {
        return loginUsername;
    }
    
    public void setLoginUsername(String loginUsername) {
        this.loginUsername = loginUsername;
    }
    
    public String getLoginPassword() {
        return loginPassword;
    }
    
    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }
    
    public String getBudget() {
        return budget;
    }
    
    public void setBudget(String budget) {
        this.budget = budget;
    }
    
    public int getLineNumber() {
        return lineNumber;
    }
    
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
    
    public boolean isValid() {
        return valid;
    }
    
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        this.valid = false;
    }
}
