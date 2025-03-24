package site.easy.to.build.crm.entity.temp;

public class TempBudget {
    private String montant;
    private String customerEmail; // Pour lier au client
    
    // Champs pour la validation et le suivi
    private int lineNumber;
    private boolean valid = true;
    private String errorMessage;
    
    // Constructeur par défaut
    public TempBudget() {
    }
    
    // Constructeur avec tous les champs
    public TempBudget(String montant, String customerEmail, int lineNumber) {
        this.montant = montant;
        this.customerEmail = customerEmail;
        this.lineNumber = lineNumber;
    }
    
    // Méthode pour valider les données
    public boolean validate() {
        if (montant == null || montant.trim().isEmpty()) {
            valid = false;
            errorMessage = "Montant is required";
            return false;
        }
        
        try {
            Double.parseDouble(montant);
        } catch (NumberFormatException e) {
            valid = false;
            errorMessage = "Montant must be a valid number";
            return false;
        }
        
        if (customerEmail == null || customerEmail.trim().isEmpty()) {
            valid = false;
            errorMessage = "Customer email is required";
            return false;
        }
        
        return true;
    }
    
    // Getters et setters
    public String getMontant() {
        return montant;
    }
    
    public void setMontant(String montant) {
        this.montant = montant;
    }
    
    public String getCustomerEmail() {
        return customerEmail;
    }
    
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
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
