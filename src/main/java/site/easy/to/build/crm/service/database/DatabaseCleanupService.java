package site.easy.to.build.crm.service.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class DatabaseCleanupService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    // Tables à préserver (ne pas vider)
    private static final List<String> TABLES_TO_PRESERVE = Arrays.asList(
            "roles", 
            "users",
            "oauth_users",
            "user_roles",
            "user_profile",
            "customer",
            "customer_login_info"
    );
    
    /**
     * Efface toutes les données de la base de données sauf les tables spécifiées
     * en désactivant temporairement les contraintes de clé étrangère
     */
    @Transactional
    public void cleanupDatabase() {
        try {
            // Désactiver les contraintes de clé étrangère
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
            
            // Récupérer toutes les tables de la base de données
            List<String> tables = jdbcTemplate.queryForList(
                    "SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA = DATABASE()",
                    String.class);
            
            // Vider chaque table sauf celles à préserver
            for (String table : tables) {
                if (!TABLES_TO_PRESERVE.contains(table)) {
                    jdbcTemplate.execute("TRUNCATE TABLE " + table);
                }
            }
        } finally {
            // Réactiver les contraintes de clé étrangère
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }
}
