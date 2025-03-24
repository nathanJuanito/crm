package site.easy.to.build.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.service.budget.BudgetService;
import site.easy.to.build.crm.service.customer.CustomerService;
import site.easy.to.build.crm.util.AuthorizationUtil;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/employee/budget")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;
    
    @Autowired
    private CustomerService customerService;
    
    // Afficher tous les budgets (pour les managers seulement)
    @GetMapping("/all-budgets")
    public String showAllBudgets(Model model, Authentication authentication) {
        // Vérifier si l'utilisateur a les droits d'accès
        if (!AuthorizationUtil.hasRole(authentication, "ROLE_MANAGER")) {
            return "redirect:/access-denied";
        }
        
        List<Budget> budgets = budgetService.findAll();
        model.addAttribute("budgets", budgets);
        
        return "budget/list-budgets";
    }
    
    // Afficher les budgets d'un client spécifique
    @GetMapping("/customer/{customerId}")
    public String showCustomerBudgets(@PathVariable("customerId") Integer customerId, 
                                     Model model, 
                                     Authentication authentication) {
        // Vérifier si l'utilisateur a les droits d'accès
        if (!AuthorizationUtil.hasRole(authentication, "ROLE_MANAGER")) {
            return "redirect:/access-denied";
        }
        
        // Vérifier si le client existe
        Customer customer = customerService.findByCustomerId(customerId);
        if (customer == null) {
            return "redirect:/employee/budget/all-budgets";
        }
        
        List<Budget> budgets = budgetService.findByCustomerId(customerId);
        model.addAttribute("customer", customer);
        model.addAttribute("budgets", budgets);
        
        return "budget/customer-budgets";
    }
    
    // Afficher le formulaire de création de budget
    @GetMapping("/create-budget")
    public String showCreateBudgetForm(Model model, Authentication authentication) {
        // Récupérer tous les clients
        List<Customer> customers = customerService.findAll();
        
        model.addAttribute("budget", new Budget());
        model.addAttribute("customers", customers);
        
        return "budget/budget-form";
    }
    
    // Traiter le formulaire de création de budget
    @PostMapping("/save")
    public String saveBudget(@RequestParam("customerId") Integer customerId,
                            @RequestParam("montant") BigDecimal montant,
                            RedirectAttributes redirectAttributes,
                            Authentication authentication) {
        // Vérifier si l'utilisateur a les droits d'accès
        if (!AuthorizationUtil.hasRole(authentication, "ROLE_MANAGER")) {
            return "redirect:/access-denied";
        }
        
        try {
            // Récupérer le client
            Customer customer = customerService.findByCustomerId(customerId);
            if (customer == null) {
                redirectAttributes.addFlashAttribute("error", "Client introuvable");
                return "redirect:/employee/budget/create-budget";
            }
            
            // Créer et sauvegarder le budget
            Budget budget = new Budget(montant, customer);
            budgetService.save(budget);
            
            redirectAttributes.addFlashAttribute("success", "Budget créé avec succès");
            return "redirect:/employee/budget/create-budget";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création du budget: " + e.getMessage());
            return "redirect:/employee/budget/create-budget";
        }
    }
    
    // Afficher le formulaire de modification de budget
    @GetMapping("/edit/{budgetId}")
    public String showEditBudgetForm(@PathVariable("budgetId") Integer budgetId,
                                    Model model,
                                    Authentication authentication) {
        // Vérifier si l'utilisateur a les droits d'accès
        if (!AuthorizationUtil.hasRole(authentication, "ROLE_MANAGER")) {
            return "redirect:/access-denied";
        }
        
        try {
            // Récupérer le budget
            Budget budget = budgetService.findById(budgetId);
            
            model.addAttribute("budget", budget);
            
            return "budget/edit-budget-form";
            
        } catch (Exception e) {
            return "redirect:/employee/budget/all-budgets";
        }
    }
    
    // Traiter le formulaire de modification de budget
    @PostMapping("/update")
    public String updateBudget(@RequestParam("budgetId") Integer budgetId,
                              @RequestParam("montant") BigDecimal montant,
                              RedirectAttributes redirectAttributes,
                              Authentication authentication) {
        // Vérifier si l'utilisateur a les droits d'accès
        if (!AuthorizationUtil.hasRole(authentication, "ROLE_MANAGER")) {
            return "redirect:/access-denied";
        }
        
        try {
            // Récupérer le budget
            Budget budget = budgetService.findById(budgetId);
            
            // Mettre à jour le budget
            budget.setMontant(montant);
            budgetService.save(budget);
            
            redirectAttributes.addFlashAttribute("success", "Budget mis à jour avec succès");
            return "redirect:/employee/budget/customer/" + budget.getCustomer().getCustomerId();
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour du budget: " + e.getMessage());
            return "redirect:/employee/budget/all-budgets";
        }
    }
    
    // Supprimer un budget
    @PostMapping("/delete/{budgetId}")
    public String deleteBudget(@PathVariable("budgetId") Integer budgetId,
                              RedirectAttributes redirectAttributes,
                              Authentication authentication) {
        // Vérifier si l'utilisateur a les droits d'accès
        if (!AuthorizationUtil.hasRole(authentication, "ROLE_MANAGER")) {
            return "redirect:/access-denied";
        }
        
        try {
            // Récupérer le budget
            Budget budget = budgetService.findById(budgetId);
            // Supprimer le budget
            budgetService.deleteById(budgetId);
            
            redirectAttributes.addFlashAttribute("success", "Budget supprimé avec succès");
            return "redirect:/employee/budget/all-budgets";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression du budget: " + e.getMessage());
            return "redirect:/employee/budget/all-budgets";
        }
    }
}
