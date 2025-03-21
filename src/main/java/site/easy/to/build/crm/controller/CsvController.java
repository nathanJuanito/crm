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
import site.easy.to.build.crm.service.lead.LeadService;
import site.easy.to.build.crm.service.ticket.TicketService;
import site.easy.to.build.crm.service.contract.ContractService;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.entity.Contract;

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
}
