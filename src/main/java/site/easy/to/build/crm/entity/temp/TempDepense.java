package site.easy.to.build.crm.entity.temp;

public class TempDepense {
    private String montant;
    private String leadName; // Pour lier au lead (optionnel)
    private String ticketSubject; // Pour lier au ticket (optionnel)
    
    // Champs pour la validation et le suivi
    private int lineNumber;
    private boolean valid = true;
    private String errorMessage;
    
    // Constructeur par défaut
    public TempDepense() {
    }
    
    // Constructeur avec tous les champs
    public TempDepense(String montant, String leadName, String ticketSubject, int lineNumber) {
        this.montant = montant;
        this.leadName = leadName;
        this.ticketSubject = ticketSubject;
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
        
        // Au moins un lead ou un ticket doit être spécifié
        if ((leadName == null || leadName.trim().isEmpty()) && 
            (ticketSubject == null || ticketSubject.trim().isEmpty())) {
            valid = false;
            errorMessage = "Either lead name or ticket subject must be specified";
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
    
    public String getLeadName() {
        return leadName;
    }
    
    public void setLeadName(String leadName) {
        this.leadName = leadName;
    }
    
    public String getTicketSubject() {
        return ticketSubject;
    }
    
    public void setTicketSubject(String ticketSubject) {
        this.ticketSubject = ticketSubject;
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
