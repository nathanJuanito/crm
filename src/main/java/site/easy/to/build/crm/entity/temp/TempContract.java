package site.easy.to.build.crm.entity.temp;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class TempContract {
    private String subject;
    private String description;
    private String status;
    private String startDate;
    private String endDate;
    private String amount;
    private String customerEmail; // Pour lier au client
    private String leadName; // Pour lier au lead (optionnel)
    private String googleDrive; // Flag pour Google Drive (true/false)
    
    // Champs pour la validation et le suivi
    private int lineNumber;
    private boolean valid = true;
    private String errorMessage;
    
    // Constructeur par défaut
    public TempContract() {
    }
    
    // Constructeur avec tous les champs
    public TempContract(String subject, String description, String status, 
                       String startDate, String endDate, String amount, 
                       String customerEmail, String leadName, String googleDrive, 
                       int lineNumber) {
        this.subject = subject;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.amount = amount;
        this.customerEmail = customerEmail;
        this.leadName = leadName;
        this.googleDrive = googleDrive;
        this.lineNumber = lineNumber;
    }
    
    // Méthode pour valider les données
    public boolean validate() {
        if (subject == null || subject.trim().isEmpty()) {
            valid = false;
            errorMessage = "Subject is required";
            return false;
        }
        
        if (customerEmail == null || customerEmail.trim().isEmpty()) {
            valid = false;
            errorMessage = "Customer email is required";
            return false;
        }
        
        if (status == null || status.trim().isEmpty()) {
            valid = false;
            errorMessage = "Status is required";
            return false;
        }
        
        // Validation des dates
        if (startDate != null && !startDate.trim().isEmpty()) {
            try {
                LocalDate.parse(startDate);
            } catch (DateTimeParseException e) {
                valid = false;
                errorMessage = "Invalid start date format";
                return false;
            }
        }
        
        if (endDate != null && !endDate.trim().isEmpty()) {
            try {
                LocalDate.parse(endDate);
            } catch (DateTimeParseException e) {
                valid = false;
                errorMessage = "Invalid end date format";
                return false;
            }
        }
        
        // Validation du montant
        if (amount != null && !amount.trim().isEmpty()) {
            try {
                Double.parseDouble(amount);
            } catch (NumberFormatException e) {
                valid = false;
                errorMessage = "Amount must be a valid number";
                return false;
            }
        }
        
        // Validation du flag Google Drive (si présent)
        if (googleDrive != null && !googleDrive.trim().isEmpty()) {
            if (!googleDrive.equalsIgnoreCase("true") && !googleDrive.equalsIgnoreCase("false")) {
                valid = false;
                errorMessage = "Google Drive flag must be true or false";
                return false;
            }
        }
        
        return true;
    }
    
    // Getters et setters
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
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
