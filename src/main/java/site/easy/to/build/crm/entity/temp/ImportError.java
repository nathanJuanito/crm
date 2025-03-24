package site.easy.to.build.crm.entity.temp;

public class ImportError {
    private String entityType; // Type d'entité (User, Customer, etc.)
    private String field; // Champ concerné
    private String value; // Valeur problématique
    private String message; // Message d'erreur
    private int lineNumber; // Numéro de ligne dans le CSV
    
    // Constructeur par défaut
    public ImportError() {
    }
    
    // Constructeur avec tous les champs
    public ImportError(String entityType, String field, String value, String message, int lineNumber) {
        this.entityType = entityType;
        this.field = field;
        this.value = value;
        this.message = message;
        this.lineNumber = lineNumber;
    }
    
    // Getters et setters
    public String getEntityType() {
        return entityType;
    }
    
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
    
    public String getField() {
        return field;
    }
    
    public void setField(String field) {
        this.field = field;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public int getLineNumber() {
        return lineNumber;
    }
    
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
    
    @Override
    public String toString() {
        return "Line " + lineNumber + ": [" + entityType + "] " + field + " - " + message + " (value: " + value + ")";
    }
}
