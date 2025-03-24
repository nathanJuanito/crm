package site.easy.to.build.crm.entity.temp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class TempUser {
    private String email;
    private String username;
    private String password;
    private String hireDate;
    private String status;
    
    // Champs pour le profil utilisateur
    private String firstName;
    private String lastName;
    private String phone;
    private String department;
    private String salary;
    private String country;
    private String position;
    private String address;
    
    // Rôles (sous forme de chaîne séparée par des virgules)
    private String roles;
    
    // Champs pour la validation et le suivi
    private int lineNumber;
    private boolean valid = true;
    private String errorMessage;
    
    // Constructeur par défaut
    public TempUser() {
    }
    
    // Constructeur avec tous les champs
    public TempUser(String email, String username, String password, String hireDate, 
                   String status, String firstName, String lastName, String phone, 
                   String department, String salary, String country, String position, 
                   String address, String roles, int lineNumber) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.hireDate = hireDate;
        this.status = status;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.department = department;
        this.salary = salary;
        this.country = country;
        this.position = position;
        this.address = address;
        this.roles = roles;
        this.lineNumber = lineNumber;
    }
    
    // Méthode pour valider les données
    public boolean validate() {
        if (email == null || email.trim().isEmpty()) {
            valid = false;
            errorMessage = "Email is required";
            return false;
        }
        
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            valid = false;
            errorMessage = "Invalid email format";
            return false;
        }
        
        if (username == null || username.trim().isEmpty()) {
            valid = false;
            errorMessage = "Username is required";
            return false;
        }
        
        if (roles == null || roles.trim().isEmpty()) {
            valid = false;
            errorMessage = "At least one role is required";
            return false;
        }
        
        // Validation du salaire (si présent)
        if (salary != null && !salary.trim().isEmpty()) {
            try {
                new BigDecimal(salary);
            } catch (NumberFormatException e) {
                valid = false;
                errorMessage = "Salary must be a valid number";
                return false;
            }
        }
        
        // Validation de la date d'embauche (si présente)
        if (hireDate != null && !hireDate.trim().isEmpty()) {
            try {
                LocalDateTime.parse(hireDate);
            } catch (DateTimeParseException e) {
                valid = false;
                errorMessage = "Invalid hire date format";
                return false;
            }
        }
        
        return true;
    }
    
    // Getters et setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    // Autres getters et setters...
    
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
