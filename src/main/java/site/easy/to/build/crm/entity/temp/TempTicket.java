package site.easy.to.build.crm.entity.temp;

public class TempTicket {
    private String subject;
    private String description;
    private String status;
    private String priority;
    private String customerEmail; // Pour lier au client
    private String managerEmail; // Pour lier au manager
    private String employeeEmail; // Pour lier à l'employé
    private String depense; // Montant de la dépense associée
    
    // Champs pour la validation et le suivi
    private int lineNumber;
    private boolean valid = true;
    private String errorMessage;
    
    // Constructeur par défaut
    public TempTicket() {
    }
    
    // Constructeur avec tous les champs
    public TempTicket(String subject, String description, String status, 
                     String priority, String customerEmail, String managerEmail, 
                     String employeeEmail, String depense, int lineNumber) {
        this.subject = subject;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.customerEmail = customerEmail;
        this.managerEmail = managerEmail;
        this.employeeEmail = employeeEmail;
        this.depense = depense;
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
        
        if (priority == null || priority.trim().isEmpty()) {
            valid = false;
            errorMessage = "Priority is required";
            return false;
        }
        
        // Validation de la dépense (si présente)
        if (depense != null && !depense.trim().isEmpty()) {
            try {
                Double.parseDouble(depense);
            } catch (NumberFormatException e) {
                valid = false;
                errorMessage = "Depense must be a valid number";
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
