package site.easy.to.build.crm.service.data;

public interface DataGenerationService {
    
    /**
     * Génère un nombre spécifié de clients avec des données aléatoires
     * @param count Nombre de clients à générer
     * @return Nombre de clients effectivement générés
     */
    int generateCustomers(int count);
    
    /**
     * Génère un nombre spécifié de prospects avec des données aléatoires
     * @param count Nombre de prospects à générer
     * @return Nombre de prospects effectivement générés
     */
    int generateLeads(int count);
    
    /**
     * Génère un nombre spécifié de tickets avec des données aléatoires
     * @param count Nombre de tickets à générer
     * @return Nombre de tickets effectivement générés
     */
    int generateTickets(int count);
    
    /**
     * Génère un nombre spécifié de contrats avec des données aléatoires
     * @param count Nombre de contrats à générer
     * @return Nombre de contrats effectivement générés
     */
    int generateContracts(int count);
    
    /**
     * Génère un nombre spécifié de modèles d'email avec des données aléatoires
     * @param count Nombre de modèles d'email à générer
     * @return Nombre de modèles d'email effectivement générés
     */
    int generateEmailTemplates(int count);
}
