package site.easy.to.build.crm.entity.temp;

public class TempLead {
    private String name;
    private String phone;
    private String status;
    private String meetingId;
    private String customerEmail; // Pour lier au client
    private String employeeEmail; // Pour lier à l'employé
    private String depense; // Montant de la dépense associée
    private String googleDrive; // Flag pour Google Drive (true/false)
    
    // Champs pour la validation et le suivi
    private int lineNumber;
    private boolean valid = true;
    private String errorMessage;
    
    // Constructeur par défaut
    public TempLead() {
    }
    
    // Constructeur avec tous les champs
    public TempLead(String name, String phone, String status, String meetingId, 
                   String customerEmail, String employeeEmail, String depense, 
                   String googleDrive, int lineNumber) {
        this.name = name;
        this.phone = phone;
        this.status = status;
        this.meetingId = meetingId;
        this.customerEmail = customerEmail;
        this.employeeEmail = employeeEmail;
        this.depense = depense;
        this.googleDrive = googleDrive;
        this.lineNumber = lineNumber;
    }
    
    // Méthode pour valider les données
    public boolean validate() {
        if (name == null || name.trim().isEmpty()) {
            valid = false;
            errorMessage = "Name is required";
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
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
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
