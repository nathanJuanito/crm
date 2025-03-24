package site.easy.to.build.crm.entity.temp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportResult {
    private boolean success;
    private List<ImportError> errors = new ArrayList<>();
    private int totalProcessed;
    private int totalSuccess;
    private int totalFailed;
    
    // Map pour stocker les entités temporaires par type
    private Map<String, List<Object>> tempEntities = new HashMap<>();
    
    // Constructeur par défaut
    public ImportResult() {
    }
    
    // Méthode pour ajouter une erreur
    public void addError(String entityType, String field, String value, String message, int lineNumber) {
        ImportError error = new ImportError(entityType, field, value, message, lineNumber);
        errors.add(error);
        totalFailed++;
    }
    
    // Méthode pour ajouter une entité temporaire
    public void addTempEntity(String entityType, Object entity) {
        if (!tempEntities.containsKey(entityType)) {
            tempEntities.put(entityType, new ArrayList<>());
        }
        tempEntities.get(entityType).add(entity);
        totalProcessed++;
        totalSuccess++;
    }
    
    // Méthode pour calculer le statut final
    public void calculateStatus() {
        success = totalFailed == 0 && totalProcessed > 0;
    }
    
    // Getters et setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public List<ImportError> getErrors() {
        return errors;
    }
    
    public void setErrors(List<ImportError> errors) {
        this.errors = errors;
    }
    
    public int getTotalProcessed() {
        return totalProcessed;
    }
    
    public void setTotalProcessed(int totalProcessed) {
        this.totalProcessed = totalProcessed;
    }
    
    public int getTotalSuccess() {
        return totalSuccess;
    }
    
    public void setTotalSuccess(int totalSuccess) {
        this.totalSuccess = totalSuccess;
    }
    
    public int getTotalFailed() {
        return totalFailed;
    }
    
    public void setTotalFailed(int totalFailed) {
        this.totalFailed = totalFailed;
    }
    
    public Map<String, List<Object>> getTempEntities() {
        return tempEntities;
    }
    
    public void setTempEntities(Map<String, List<Object>> tempEntities) {
        this.tempEntities = tempEntities;
    }
    
    // Méthode pour obtenir les entités d'un type spécifique
    @SuppressWarnings("unchecked")
    public <T> List<T> getEntitiesByType(String entityType, Class<T> clazz) {
        List<Object> entities = tempEntities.getOrDefault(entityType, new ArrayList<>());
        List<T> result = new ArrayList<>();
        for (Object entity : entities) {
            if (clazz.isInstance(entity)) {
                result.add((T) entity);
            }
        }
        return result;
    }
}
