package site.easy.to.build.crm.service.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;

@Service
public class DataGenerationServiceImpl implements DataGenerationService {

    private static final Logger logger = Logger.getLogger(DataGenerationServiceImpl.class.getName());

    @PersistenceContext
    private EntityManager entityManager;
    
    private final Random random = new Random();
    private final String[] firstNames = {"Jean", "Marie", "Pierre", "Sophie", "Thomas", "Isabelle", "Paul", "Nathalie", "Michel", "Claire"};
    private final String[] lastNames = {"Dupont", "Martin", "Durand", "Lefebvre", "Moreau", "Simon", "Laurent", "Michel", "Leroy", "Roux"};
    private final String[] companies = {"Acme Inc", "Globex", "Initech", "Umbrella Corp", "Stark Industries", "Wayne Enterprises", "Cyberdyne Systems", "Soylent Corp", "Massive Dynamic", "Oscorp"};
    private final String[] domains = {"gmail.com", "yahoo.fr", "hotmail.com", "outlook.com", "orange.fr", "free.fr", "sfr.fr", "example.com"};
    private final String[] phoneNumbers = {"01", "02", "03", "04", "05", "06", "07", "09"};
    private final String[] cities = {"Paris", "Lyon", "Marseille", "Toulouse", "Nice", "Nantes", "Strasbourg", "Montpellier", "Bordeaux", "Lille"};
    private final String[] streets = {"Rue de la Paix", "Avenue des Champs-Élysées", "Boulevard Saint-Michel", "Rue du Faubourg Saint-Honoré", "Place de la Concorde", "Rue de Rivoli", "Avenue Montaigne", "Rue Saint-Antoine", "Boulevard Haussmann", "Rue de Sèvres"};
    private final String[] statuses = {"NEW", "IN_PROGRESS", "COMPLETED", "CANCELLED"};
    private final String[] priorities = {"LOW", "MEDIUM", "HIGH", "URGENT"};
    private final String[] countries = {"France", "Belgique", "Suisse", "Canada", "Allemagne", "Espagne", "Italie", "Royaume-Uni"};
    private final String[] states = {"Île-de-France", "Auvergne-Rhône-Alpes", "Provence-Alpes-Côte d'Azur", "Occitanie", "Nouvelle-Aquitaine", "Grand Est", "Hauts-de-France", "Normandie"};
    
    @Override
    @Transactional
    public int generateCustomers(int count) {
        int generated = 0;
        
        for (int i = 0; i < count; i++) {
            try {
                // Créer un CustomerLoginInfo pour chaque client dans une transaction séparée
                int loginInfoId = createCustomerLoginInfo();
                
                // Créer le client avec les informations générées
                String firstName = getRandomElement(firstNames);
                String lastName = getRandomElement(lastNames);
                String fullName = firstName + " " + lastName;
                
                String sql = "INSERT INTO customer (name, phone, address, city, state, country, description, position, email, created_at, profile_id) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                
                entityManager.createNativeQuery(sql)
                    .setParameter(1, fullName)
                    .setParameter(2, generatePhoneNumber())
                    .setParameter(3, generateAddress())
                    .setParameter(4, getRandomElement(cities))
                    .setParameter(5, getRandomElement(states))
                    .setParameter(6, getRandomElement(countries))
                    .setParameter(7, "Client généré automatiquement #" + (i + 1))
                    .setParameter(8, "Position " + (i + 1))
                    .setParameter(9, generateEmail(firstName, lastName))
                    .setParameter(10, LocalDateTime.now())
                    .setParameter(11, loginInfoId)
                    .executeUpdate();
                
                generated++;
            } catch (Exception e) {
                logger.severe("Error generating customer: " + e.getMessage());
                // Continue with the next customer
            }
        }
        
        return generated;
    }
    
    @Override
    @Transactional
    public int generateLeads(int count) {
        int generated = 0;
        
        // Récupérer les clients existants
        List<Integer> customerIds = getCustomerIds();
        if (customerIds.isEmpty()) {
            // Générer quelques clients si aucun n'existe
            generateCustomers(10);
            customerIds = getCustomerIds();
        }
        
        for (int i = 0; i < count; i++) {
            try {
                // Sélectionner un client aléatoire
                int customerId = 0;
                if (!customerIds.isEmpty()) {
                    customerId = customerIds.get(random.nextInt(customerIds.size()));
                }
                
                String sql = "INSERT INTO trigger_lead (customer_id, name, phone, status, created_at) " +
                             "VALUES (?, ?, ?, ?, ?)";
                
                entityManager.createNativeQuery(sql)
                    .setParameter(1, customerId)
                    .setParameter(2, "Lead #" + UUID.randomUUID().toString().substring(0, 8))
                    .setParameter(3, generatePhoneNumber())
                    .setParameter(4, getRandomElement(statuses))
                    .setParameter(5, LocalDateTime.now())
                    .executeUpdate();
                
                generated++;
            } catch (Exception e) {
                logger.severe("Error generating lead: " + e.getMessage());
                // Continue with the next lead
            }
        }
        
        return generated;
    }
    
    @Override
    @Transactional
    public int generateTickets(int count) {
        int generated = 0;
        
        // Récupérer les clients existants
        List<Integer> customerIds = getCustomerIds();
        if (customerIds.isEmpty()) {
            // Générer quelques clients si aucun n'existe
            generateCustomers(10);
            customerIds = getCustomerIds();
        }
        
        for (int i = 0; i < count; i++) {
            try {
                // Sélectionner un client aléatoire
                int customerId = 0;
                if (!customerIds.isEmpty()) {
                    customerId = customerIds.get(random.nextInt(customerIds.size()));
                }
                
                String sql = "INSERT INTO trigger_ticket (subject, description, status, priority, customer_id, created_at) " +
                             "VALUES (?, ?, ?, ?, ?, ?)";
                
                entityManager.createNativeQuery(sql)
                    .setParameter(1, "Ticket #" + UUID.randomUUID().toString().substring(0, 8))
                    .setParameter(2, "Description générée automatiquement pour le ticket " + (i + 1))
                    .setParameter(3, getRandomElement(statuses))
                    .setParameter(4, getRandomElement(priorities))
                    .setParameter(5, customerId)
                    .setParameter(6, LocalDateTime.now())
                    .executeUpdate();
                
                generated++;
            } catch (Exception e) {
                logger.severe("Error generating ticket: " + e.getMessage());
                // Continue with the next ticket
            }
        }
        
        return generated;
    }
    
    @Override
    @Transactional
    public int generateContracts(int count) {
        int generated = 0;
        
        // Récupérer les clients existants
        List<Integer> customerIds = getCustomerIds();
        if (customerIds.isEmpty()) {
            // Générer quelques clients si aucun n'existe
            generateCustomers(10);
            customerIds = getCustomerIds();
        }
        
        // Récupérer les leads existants
        List<Integer> leadIds = getLeadIds();
        
        for (int i = 0; i < count; i++) {
            try {
                // Sélectionner un client aléatoire
                int customerId = 0;
                if (!customerIds.isEmpty()) {
                    customerId = customerIds.get(random.nextInt(customerIds.size()));
                }
                
                // Sélectionner un lead aléatoire (optionnel)
                Integer leadId = null;
                if (!leadIds.isEmpty() && random.nextBoolean()) {
                    leadId = leadIds.get(random.nextInt(leadIds.size()));
                }
                
                LocalDate startDate = LocalDate.now().minusDays(random.nextInt(30));
                LocalDate endDate = LocalDate.now().plusDays(30 + random.nextInt(335));
                
                String sql = "INSERT INTO trigger_contract (subject, description, status, start_date, end_date, amount, customer_id, lead_id, created_at) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                
                entityManager.createNativeQuery(sql)
                    .setParameter(1, "Contrat #" + UUID.randomUUID().toString().substring(0, 8))
                    .setParameter(2, "Description générée automatiquement pour le contrat " + (i + 1))
                    .setParameter(3, getRandomElement(statuses))
                    .setParameter(4, startDate)
                    .setParameter(5, endDate)
                    .setParameter(6, 1000 + random.nextInt(9000))
                    .setParameter(7, customerId)
                    .setParameter(8, leadId)
                    .setParameter(9, LocalDateTime.now())
                    .executeUpdate();
                
                generated++;
            } catch (Exception e) {
                logger.severe("Error generating contract: " + e.getMessage());
                // Continue with the next contract
            }
        }
        
        return generated;
    }
    
    @Override
    @Transactional
    public int generateEmailTemplates(int count) {
        int generated = 0;
        
        String[] templateNames = {
            "Bienvenue", "Confirmation", "Rappel", "Notification", "Mise à jour", 
            "Alerte", "Invitation", "Remerciement", "Suivi", "Rapport"
        };
        
        String baseHtmlTemplate = 
            "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
            "<h1 style='color: #333;'>{{title}}</h1>" +
            "<p>Bonjour {{name}},</p>" +
            "<p>{{content}}</p>" +
            "<p>Cordialement,<br>L'équipe CRM</p>" +
            "</div>";
        
        // Récupérer un utilisateur existant (manager)
        Integer userId = getManagerUserId();
        
        for (int i = 0; i < count; i++) {
            try {
                String templateName = getRandomElement(templateNames) + " " + UUID.randomUUID().toString().substring(0, 6);
                String content = baseHtmlTemplate
                    .replace("{{title}}", "Modèle: " + templateName)
                    .replace("{{content}}", "Ceci est un modèle d'email généré automatiquement pour démonstration.");
                
                String jsonDesign = "{\"design\":\"basic\",\"blocks\":[{\"type\":\"header\",\"data\":{\"text\":\"" + templateName + "\",\"level\":1}}," +
                                    "{\"type\":\"paragraph\",\"data\":{\"text\":\"Contenu du modèle d'email\"}}]}";
                
                String sql = "INSERT INTO email_template (name, content, user_id, json_design, created_at) " +
                             "VALUES (?, ?, ?, ?, ?)";
                
                entityManager.createNativeQuery(sql)
                    .setParameter(1, templateName)
                    .setParameter(2, content)
                    .setParameter(3, userId)
                    .setParameter(4, jsonDesign)
                    .setParameter(5, LocalDateTime.now())
                    .executeUpdate();
                
                generated++;
            } catch (Exception e) {
                logger.severe("Error generating email template: " + e.getMessage());
                // Continue with the next template
            }
        }
        
        return generated;
    }
    
    // Méthodes utilitaires
    
    private <T> T getRandomElement(T[] array) {
        return array[random.nextInt(array.length)];
    }
    
    private String generateEmail(String firstName, String lastName) {
        String normalizedFirstName = firstName.toLowerCase().replaceAll("[^a-z]", "");
        String normalizedLastName = lastName.toLowerCase().replaceAll("[^a-z]", "");
        return normalizedFirstName + "." + normalizedLastName + "@" + getRandomElement(domains);
    }
    
    private String generatePhoneNumber() {
        StringBuilder phoneNumber = new StringBuilder(getRandomElement(phoneNumbers));
        for (int i = 0; i < 8; i++) {
            phoneNumber.append(random.nextInt(10));
        }
        return phoneNumber.toString();
    }
    
    private String generateAddress() {
        return (1 + random.nextInt(100)) + " " + getRandomElement(streets) + ", " + getRandomElement(cities);
    }
    
    private String generateUsername() {
        return "user_" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    @Transactional
    private int createCustomerLoginInfo() {
        String sql = "INSERT INTO customer_login_info (username, token, password_set) VALUES (?, ?, ?)";
        
        entityManager.createNativeQuery(sql)
            .setParameter(1, generateUsername())
            .setParameter(2, UUID.randomUUID().toString())
            .setParameter(3, false)
            .executeUpdate();
        
        // Récupérer l'ID généré
        Object result = entityManager.createNativeQuery("SELECT LAST_INSERT_ID()").getSingleResult();
        return ((Number) result).intValue();
    }
        /**
     * Récupère tous les IDs des clients
     */
    private List<Integer> getCustomerIds() {
        List<Integer> customerIds = new ArrayList<>();
        try {
            List<?> results = entityManager.createNativeQuery("SELECT customer_id FROM customer").getResultList();
            for (Object result : results) {
                customerIds.add(((Number) result).intValue());
            }
        } catch (Exception e) {
            logger.severe("Error retrieving customer IDs: " + e.getMessage());
        }
        return customerIds;
    }
    
    /**
     * Récupère tous les IDs des leads
     */
    private List<Integer> getLeadIds() {
        List<Integer> leadIds = new ArrayList<>();
        try {
            List<?> results = entityManager.createNativeQuery("SELECT lead_id FROM trigger_lead").getResultList();
            for (Object result : results) {
                leadIds.add(((Number) result).intValue());
            }
        } catch (Exception e) {
            logger.severe("Error retrieving lead IDs: " + e.getMessage());
        }
        return leadIds;
    }
    
    /**
     * Récupère l'ID d'un utilisateur avec le rôle MANAGER
     */
    private Integer getManagerUserId() {
        Integer userId = null;
        try {
            Object result = entityManager.createNativeQuery(
                "SELECT u.id FROM users u " +
                "JOIN user_roles ur ON u.id = ur.user_id " +
                "JOIN roles r ON ur.role_id = r.id " +
                "WHERE r.name = 'ROLE_MANAGER' LIMIT 1"
            ).getSingleResult();
            
            if (result != null) {
                userId = ((Number) result).intValue();
            }
        } catch (Exception e) {
            logger.severe("Error retrieving manager user ID: " + e.getMessage());
        }
        return userId;
    }
}

