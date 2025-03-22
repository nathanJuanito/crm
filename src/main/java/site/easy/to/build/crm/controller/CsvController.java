package site.easy.to.build.crm.controller;

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
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.entity.Contract;
import site.easy.to.build.crm.service.data.DataGenerationService;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/csv")
public class CsvController {
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

    @PostMapping("/import")
    @Transactional
    public String importCsv(@RequestParam("file") MultipartFile file,
                           @RequestParam("entityType") String entityType,
                           RedirectAttributes redirectAttributes,
                           Authentication authentication) {
        
        // Vérifier si l'utilisateur a les droits d'accès
        if (!AuthorizationUtil.hasRole(authentication, "ROLE_MANAGER") &&
            !AuthorizationUtil.hasRole(authentication, "ROLE_EMPLOYEE")) {
            return "redirect:/access-denied";
        }
        
        // Vérifier si le type d'entité est valide
        if (!ALLOWED_ENTITY_TYPES.contains(entityType)) {
            redirectAttributes.addFlashAttribute("error", "Type d'entité non valide");
            return "redirect:/csv";
        }
        
        // Vérifier si le fichier est vide
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Veuillez sélectionner un fichier à importer");
            return "redirect:/csv";
        }
        
        // Vérifier si le fichier est au format CSV
        if (!csvHelper.isCSVFile(file)) {
            redirectAttributes.addFlashAttribute("error", "Le fichier doit être au format CSV");
            return "redirect:/csv";
        }
        
        try {
            int importedCount = 0;
            
            switch (entityType) {
                case "customers":
                    List<Customer> customers = csvHelper.csvToCustomers(file.getInputStream());
                    for (Customer customer : customers) {
                        customerService.save(customer);
                    }
                    importedCount = customers.size();
                    break;
                    
                case "leads":
                    List<Lead> leads = csvHelper.csvToLeads(file.getInputStream());
                    for (Lead lead : leads) {
                        leadService.save(lead);
                    }
                    importedCount = leads.size();
                    break;
                    
                case "tickets":
                    List<Ticket> tickets = csvHelper.csvToTickets(file.getInputStream());
                    for (Ticket ticket : tickets) {
                        ticketService.save(ticket);
                    }
                    importedCount = tickets.size();
                    break;
                    
                case "contracts":
                    List<Contract> contracts = csvHelper.csvToContracts(file.getInputStream());
                    for (Contract contract : contracts) {
                        contractService.save(contract);
                    }
                    importedCount = contracts.size();
                    break;
                    
                default:
                    redirectAttributes.addFlashAttribute("error", "Type d'entité non pris en charge");
                    return "redirect:/csv";
            }
            
            redirectAttributes.addFlashAttribute("success",
                    importedCount + " enregistrements importés avec succès pour " + entityType);
                    
        } catch (Exception e) {
            // En cas d'erreur, annuler la transaction
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'importation: " + e.getMessage());
        }
        
        return "redirect:/csv";
    }

    @GetMapping("/export/{entityType}")
    public void exportCsv(@PathVariable("entityType") String entityType,
                         HttpServletResponse response,
                         Authentication authentication) throws IOException {
        
        // Vérifier si l'utilisateur a les droits d'accès
        if (!AuthorizationUtil.hasRole(authentication, "ROLE_MANAGER") &&
            !AuthorizationUtil.hasRole(authentication, "ROLE_EMPLOYEE")) {
            response.sendRedirect("/access-denied");
            return;
        }
        
        // Vérifier si le type d'entité est valide
        if (!ALLOWED_ENTITY_TYPES.contains(entityType)) {
            response.sendRedirect("/csv");
            return;
        }
        
        // Configuration de la réponse HTTP pour le téléchargement du fichier CSV
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + entityType + ".csv\"");
        
        // Exportation réelle des données selon le type d'entité
        try {
            switch (entityType) {
                case "customers":
                    List<Customer> customers = customerService.findAll();
                    csvHelper.exportCustomersToCsv(customers, response.getWriter());
                    break;
                case "leads":
                    List<Lead> leads = leadService.findAll();
                    csvHelper.exportLeadsToCsv(leads, response.getWriter());
                    break;
                case "tickets":
                    List<Ticket> tickets = ticketService.findAll();
                    csvHelper.exportTicketsToCsv(tickets, response.getWriter());
                    break;
                case "contracts":
                    List<Contract> contracts = contractService.findAll();
                    csvHelper.exportContractsToCsv(contracts, response.getWriter());
                    break;
                default:
                    response.getWriter().write("Type d'entité non pris en charge");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Erreur lors de l'exportation: " + e.getMessage());
        }
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
