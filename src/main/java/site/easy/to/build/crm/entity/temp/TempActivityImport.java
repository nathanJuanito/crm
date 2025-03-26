package site.easy.to.build.crm.entity.temp;

import com.opencsv.bean.CsvBindByName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.AssertTrue;

public class TempActivityImport {
   
    @CsvBindByName(column = "customer_email")
    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    private String customerEmail; // Renommé pour correspondre aux getters/setters
   
    @CsvBindByName(column = "subject_or_name")
    @NotBlank(message = "Subject or name is required")
    private String subjectOrName; // Renommé pour correspondre aux getters/setters
   
    @CsvBindByName(column = "type")
    @NotBlank(message = "Type is required")
    private String type;
   
    @CsvBindByName(column = "status")
    @NotBlank(message = "Status is required")
    private String status;
   
    @CsvBindByName(column = "expense")
    @Pattern(regexp = "^[+]?\\d+([.,]\\d+)?$", message = "Expense must be a positive number")
    private String expense;
   
    // Ligne du fichier pour le reporting d'erreurs
    private int lineNumber;
    // Nom du fichier pour le reporting d'erreurs
    private String fileName;
   
    // // Méthode de validation personnalisée pour le statut en fonction du type
    // @AssertTrue(message = "Invalid status for the specified type")
    // public boolean isStatusValidForType() {
    //     if (type == null || status == null) {
    //         return true; // Laissez les autres validations gérer les valeurs nulles
    //     }
        
    //     if ("ticket".equalsIgnoreCase(type)) {
    //         return status.matches("^(open|assigned|on-hold|in-progress|resolved|closed|reopened|pending-customer-response|escalated|archived|meeting-to-schedule|archived|success|assign-to-sales)$");
    //     } else if ("lead".equalsIgnoreCase(type)) {
    //         return status.matches("^(meeting-to-schedule|archived|success|assign-to-sales|open|assigned|on-hold|in-progress|resolved|closed|reopened|pending-customer-response|escalated|archived)$");
    //     }
        
    //     return false; // Type non reconnu
    // }
    
    // // Méthode pour obtenir le message d'erreur approprié pour le statut
    // public String getStatusValidationMessage() {
    //     if ("ticket".equalsIgnoreCase(type)) {
    //         return "For ticket type, status must be one of: open, assigned, on-hold, in-progress, resolved, closed, reopened, pending-customer-response, escalated, archived";
    //     } else if ("lead".equalsIgnoreCase(type)) {
    //         return "For lead type, status must be one of: meeting-to-schedule, scheduled, success, assign-to-sales";
    //     }
    //     return "Invalid status for the specified type";
    // }
   
    // Constructeurs, getters et setters
    public TempActivityImport() {}
   
    public TempActivityImport(String customerEmail, String subjectOrName,
                             String type, String status, String expense) {
        this.customerEmail = customerEmail;
        this.subjectOrName = subjectOrName;
        this.type = type;
        this.status = status;
        this.expense = expense;
    }
   
    // Getters et setters
    public String getCustomerEmail() {
        return customerEmail;
    }
   
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
   
    public String getSubjectOrName() {
        return subjectOrName;
    }
   
    public void setSubjectOrName(String subjectOrName) {
        this.subjectOrName = subjectOrName;
    }
   
    public String getType() {
        return type;
    }
   
    public void setType(String type) {
        this.type = type;
    }
   
    public String getStatus() {
        return status;
    }
   
    public void setStatus(String status) {
        this.status = status;
    }
   
    public String getExpense() {
        return expense;
    }
   
    public void setExpense(String expense) {
        this.expense = expense;
    }
   
    public int getLineNumber() {
        return lineNumber;
    }
   
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
   
    public String getFileName() {
        return fileName;
    }
   
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
   
    @Override
    public String toString() {
        return "TempActivityImport{" +
                "customerEmail='" + customerEmail + '\'' +
                ", subjectOrName='" + subjectOrName + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", expense='" + expense + '\'' +
                ", lineNumber=" + lineNumber +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
