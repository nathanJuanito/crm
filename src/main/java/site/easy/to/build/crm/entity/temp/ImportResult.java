package site.easy.to.build.crm.entity.temp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportResult {
    private List<ImportError> errors = new ArrayList<>();
    private int totalProcessed = 0;
    private int successfullyImported = 0;
    private int customersCreated = 0;
    private int leadsCreated = 0;
    private int ticketsCreated = 0;
    private int expensesCreated = 0;
    
    // Méthodes pour ajouter des erreurs et incrémenter les compteurs
    public void addError(ImportError error) {
        errors.add(error);
    }
    
    public void incrementTotalProcessed() {
        totalProcessed++;
    }
    
    public void incrementSuccessfullyImported() {
        successfullyImported++;
    }
    
    public void incrementCustomersCreated() {
        customersCreated++;
    }
    
    public void incrementLeadsCreated() {
        leadsCreated++;
    }
    
    public void incrementTicketsCreated() {
        ticketsCreated++;
    }
    
    public void incrementExpensesCreated() {
        expensesCreated++;
    }
    
    // Getters
    public List<ImportError> getErrors() {
        return errors;
    }
    
    public int getTotalProcessed() {
        return totalProcessed;
    }
    
    public int getSuccessfullyImported() {
        return successfullyImported;
    }
    
    public int getCustomersCreated() {
        return customersCreated;
    }
    
    public int getLeadsCreated() {
        return leadsCreated;
    }
    
    public int getTicketsCreated() {
        return ticketsCreated;
    }
    
    public int getExpensesCreated() {
        return expensesCreated;
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
