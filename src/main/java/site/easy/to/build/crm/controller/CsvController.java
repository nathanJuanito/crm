package site.easy.to.build.crm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import site.easy.to.build.crm.util.AuthorizationUtil;
import site.easy.to.build.crm.util.CsvHelper;
import site.easy.to.build.crm.service.customer.CustomerService;
import site.easy.to.build.crm.service.database.DatabaseCleanupService;
import site.easy.to.build.crm.service.lead.LeadService;
import site.easy.to.build.crm.service.ticket.TicketService;
import site.easy.to.build.crm.service.contract.ContractService;
import site.easy.to.build.crm.service.csv.CsvImportService;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.entity.temp.ImportError;
import site.easy.to.build.crm.entity.temp.TempActivityImport;
import site.easy.to.build.crm.entity.temp.TempBudgetImport;
import site.easy.to.build.crm.entity.temp.TempCustomerImport;
import site.easy.to.build.crm.entity.Contract;
import site.easy.to.build.crm.service.data.DataGenerationService;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/csv")
public class CsvController {

    private static final Logger logger = LoggerFactory.getLogger(CsvController.class);

    @Autowired
    private DataGenerationService dataGenerationService;

    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private LeadService leadService;
    
    @Autowired
    private TicketService ticketService;
    
    @Autowired
    private ContractService contractService;
    
    @Autowired
    private CsvHelper csvHelper;

    @Autowired
    private CsvImportService csvImportService;

    private static final List<String> ALLOWED_ENTITY_TYPES = Arrays.asList(
            "customers", "leads", "tickets", "contracts");

    @GetMapping
    public String showCsvPage(Model model, Authentication authentication) {
        // Vérifier si l'utilisateur a les droits d'accès
        if (!AuthorizationUtil.hasRole(authentication, "ROLE_MANAGER") &&
            !AuthorizationUtil.hasRole(authentication, "ROLE_EMPLOYEE")) {
            return "redirect:/access-denied";
        }
        
        model.addAttribute("entityTypes", ALLOWED_ENTITY_TYPES);
        return "csv/csv-management";
    }
    /**
     * Process the imported files
     * 
     * @param file1 the first CSV file
     * @param file2 the second CSV file
     * @param redirectAttributes for flash attributes
     * @return redirect to the import form
     */
    @PostMapping("/import")
    public String processImport(
            @RequestParam("file1") MultipartFile file1,
            @RequestParam("file2") MultipartFile file2,
            @RequestParam("file3") MultipartFile file3,
            RedirectAttributes redirectAttributes,Model model,Authentication authentication) {
        
        try {
        // Parser les fichiers
        List<TempCustomerImport> customers = csvImportService.parseCsvToEntities(file1, TempCustomerImport.class);
        List<TempActivityImport> activities = csvImportService.parseCsvToEntities(file2, TempActivityImport.class);
        List<TempBudgetImport> budgets = csvImportService.parseCsvToEntities(file3, TempBudgetImport.class);
        
        // Valider les entités
        List<ImportError> customerErrors = csvImportService.validateEntities(customers, file1.getOriginalFilename());
        List<ImportError> activityErrors = csvImportService.validateEntities(activities, file2.getOriginalFilename());
        List<ImportError> budgetErrors = csvImportService.validateEntities(budgets, file3.getOriginalFilename());
        
        // Combiner les erreurs
        List<ImportError> allErrors = new ArrayList<>();
        allErrors.addAll(customerErrors);
        allErrors.addAll(activityErrors);
        allErrors.addAll(budgetErrors);
        
        // Vérifier s'il y a des erreurs
        if (!allErrors.isEmpty()) {
            redirectAttributes.addFlashAttribute("errors", allErrors);
            redirectAttributes.addFlashAttribute("error", 
                    String.format("Import completed with %d validation errors.", allErrors.size()));
            
                    model.addAttribute("customersErrors", customerErrors);
                    model.addAttribute("activityErrors", activityErrors);
                    model.addAttribute("budgetErrors", budgetErrors);
                    
            return "csv/show-errors";
        }
        
        // Si pas d'erreurs, procéder à l'importation
        if (allErrors.isEmpty()) {
            // Importer les clients d'abord
            int importedCustomers = csvImportService.importCustomers(customers,authentication);
            
            // Puis importer les activités
            int importedActivities = csvImportService.importActivities(activities,authentication);

            // Puis importer les budgets
            int importedBudgets = csvImportService.importBudget(budgets);
            
            // Ajouter un message de succès avec les statistiques
            redirectAttributes.addFlashAttribute("success", 
                String.format("Import completed successfully. Imported %d customers , %d activities and % importedBudgets.", 
                    importedCustomers, importedActivities,importedBudgets));;
                    
            return "redirect:/csv";
        }

        
    } catch (Exception e) {
        // Gérer les exceptions...
    }
        redirectAttributes.addFlashAttribute("success", "Import completed successfully.");
        return "redirect:/csv";
    }

    @PostMapping("/export/{idCustomer}")
    public void exportCsv(@PathVariable("idCustomer") int idCustomer,
                         HttpServletResponse response,
                         Authentication authentication,
                         RedirectAttributes redirectAttributes) throws IOException {
        
        // Vérifier si l'utilisateur a les droits d'accès
        if (!AuthorizationUtil.hasRole(authentication, "ROLE_MANAGER") &&
            !AuthorizationUtil.hasRole(authentication, "ROLE_EMPLOYEE")) {
            response.sendRedirect("/access-denied");
        }
        
        // // Vérifier si le type d'entité est valide
        // if (!ALLOWED_ENTITY_TYPES.contains(entityType)) {
        //     response.sendRedirect("/csv");
        //     return;
        // }
        
        // Configuration de la réponse HTTP pour le téléchargement du fichier CSV
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + "customer" + ".csv\"");
                Customer customer = customerService.findByCustomerId(idCustomer);
                csvHelper.exportToCsv(customer, response.getWriter());
    }
    
    @PostMapping("/delete-data")
    @Transactional
    public String deleteData(@RequestParam("tableToDelete") String tableToDelete,
                             @RequestParam("confirmationText") String confirmationText,
                             @RequestParam(value = "confirmDeletion", required = false) boolean confirmDeletion,
                             RedirectAttributes redirectAttributes,
                             Authentication authentication) {
       
        // Vérifier si l'utilisateur a les droits d'accès (uniquement ROLE_MANAGER pour la suppression)
        if (!AuthorizationUtil.hasRole(authentication, "ROLE_MANAGER")) {
            return "redirect:/access-denied";
        }
        
        if (!confirmDeletion || !confirmationText.equals("DELETE")) {
            redirectAttributes.addFlashAttribute("error", "Deletion not confirmed properly");
            return "redirect:/csv";
        }

        // Liste des tables autorisées pour la suppression
        List<String> allowedTables = Arrays.asList(
                "customer", "trigger_lead", "trigger_ticket", "trigger_contract", 
                "email_template", "google_drive_file");
        
        if (!allowedTables.contains(tableToDelete)) {
            redirectAttributes.addFlashAttribute("error", "Table non autorisée pour la suppression");
            return "redirect:/csv";
        }

        try {
            // Using native query for direct table deletion
            int deletedCount = entityManager.createNativeQuery("DELETE FROM " + tableToDelete).executeUpdate();
           
            redirectAttributes.addFlashAttribute("success",
                    "Successfully deleted all data from " + tableToDelete + " (" + deletedCount + " records)");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error deleting data: " + e.getMessage());
        }

        return "redirect:/csv";
    }
    // Ajouter cette méthode à votre CsvController existant

    @Autowired
    private DatabaseCleanupService databaseCleanupService;

    @PostMapping("/cleanup-database")
    public String cleanupDatabase(RedirectAttributes redirectAttributes, Authentication authentication) {
        // Vérifier que seul un manager peut effectuer cette opération
        if (!AuthorizationUtil.hasRole(authentication, "ROLE_MANAGER")) {
            return "redirect:/access-denied";
        }
        
        try {
            databaseCleanupService.cleanupDatabase();
            redirectAttributes.addFlashAttribute("success", "La base de données a été nettoyée avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du nettoyage de la base de données: " + e.getMessage());
        }
        
        return "redirect:/csv";
    }
    
    @PostMapping("/generate-data")
    @Transactional
    public String generateData(@RequestParam("tableToGenerate") String tableToGenerate,
                            @RequestParam("recordCount") int recordCount,
                            @RequestParam(value = "confirmGeneration", required = false) boolean confirmGeneration,
                            RedirectAttributes redirectAttributes,
                            Authentication authentication) {
        
        // Vérifier si l'utilisateur a les droits d'accès
        if (!AuthorizationUtil.hasRole(authentication, "ROLE_MANAGER") &&
            !AuthorizationUtil.hasRole(authentication, "ROLE_EMPLOYEE")) {
            return "redirect:/access-denied";
        }
        
        if (!confirmGeneration) {
            redirectAttributes.addFlashAttribute("error", "Veuillez confirmer la génération de données");
            return "redirect:/csv";
        }
        
        // Limiter le nombre d'enregistrements à générer
        if (recordCount <= 0 || recordCount > 1000) {
            redirectAttributes.addFlashAttribute("error", "Le nombre d'enregistrements doit être compris entre 1 et 1000");
            return "redirect:/csv";
        }
        
        try {
            int generatedCount = 0;
            
            switch (tableToGenerate) {
                case "customers":
                    generatedCount = dataGenerationService.generateCustomers(recordCount);
                    redirectAttributes.addFlashAttribute("success", 
                        generatedCount + " clients générés avec succès");
                    break;
                    
                case "leads":
                    generatedCount = dataGenerationService.generateLeads(recordCount);
                    redirectAttributes.addFlashAttribute("success", 
                        generatedCount + " prospects générés avec succès");
                    break;
                    
                case "tickets":
                    generatedCount = dataGenerationService.generateTickets(recordCount);
                    redirectAttributes.addFlashAttribute("success", 
                        generatedCount + " tickets générés avec succès");
                    break;
                    
                case "contracts":
                    generatedCount = dataGenerationService.generateContracts(recordCount);
                    redirectAttributes.addFlashAttribute("success", 
                        generatedCount + " contrats générés avec succès");
                    break;
                    
                case "email_templates":
                    generatedCount = dataGenerationService.generateEmailTemplates(recordCount);
                    redirectAttributes.addFlashAttribute("success", 
                        generatedCount + " modèles d'email générés avec succès");
                    break;
                    
                default:
                    redirectAttributes.addFlashAttribute("error", "Type de table non valide pour la génération de données");
                    return "redirect:/csv";
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la génération des données: " + e.getMessage());
        }
        
        return "redirect:/csv";
    }
}
