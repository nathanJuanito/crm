package site.easy.to.build.crm.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.service.customer.CustomerService;
import site.easy.to.build.crm.service.depense.DepenseService;
import site.easy.to.build.crm.service.lead.LeadService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leads")
public class LeadApiController {

    private final LeadService leadService;
    private final CustomerService customerService;
    private final DepenseService depenseService;

    @Autowired
    public LeadApiController(LeadService leadService, CustomerService customerService, DepenseService depenseService) {
        this.leadService = leadService;
        this.customerService = customerService;
        this.depenseService = depenseService;
    }

    // Récupérer tous les leads
    @GetMapping
    public ResponseEntity<List<Lead>> getAllLeads() {
        List<Lead> leads = leadService.findAllWithTotalAmount();
        return ResponseEntity.ok(leads);
    }

    // Récupérer un lead par ID
    @GetMapping("/{id}")
    public ResponseEntity<Lead> getLeadById(@PathVariable int id) {
        Lead lead = leadService.findByLeadId(id);
        if (lead == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(lead);
    }

    // Récupérer un lead par ID de réunion
    @GetMapping("/meeting/{meetingId}")
    public ResponseEntity<Lead> getLeadByMeetingId(@PathVariable String meetingId) {
        Lead lead = leadService.findByMeetingId(meetingId);
        if (lead == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(lead);
    }

    // Créer un nouveau lead
    @PostMapping
    public ResponseEntity<Lead> createLead(@RequestBody Lead lead) {
        Lead savedLead = leadService.save(lead);
        return new ResponseEntity<>(savedLead, HttpStatus.CREATED);
    }

    // Mettre à jour un lead existant
    @PutMapping("/{leadId}")
    public ResponseEntity<?> updateLeadMontant(
            @PathVariable("leadId") int leadId,
            @RequestParam("montant") String montantStr) {
        
        try {
            // Vérifier si le ticket existe
            Lead lead = leadService.findByLeadId(leadId);
            if (lead == null) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "lead non trouvé avec l'ID: " + leadId);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            
            // Convertir le montant en BigDecimal
            BigDecimal montant;
            try {
                montant = new BigDecimal(montantStr);
            } catch (NumberFormatException e) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Format de montant invalide: " + montantStr);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            
            int ret = depenseService.updateLeadMontant(leadId,montant);
           
            return new ResponseEntity<>(ret, HttpStatus.OK);
            
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Erreur lors de la mise à jour du montant: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Supprimer un lead
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLead(@PathVariable int id) {
        Lead lead = leadService.findByLeadId(id);
        if (lead == null) {
            return ResponseEntity.notFound().build();
        }
        depenseService.deleteByLeadId(lead.getLeadId());;
        leadService.delete(lead);
        return ResponseEntity.noContent().build();
    }

    // Récupérer les leads d'un manager
    @GetMapping("/manager/{managerId}")
    public ResponseEntity<List<Lead>> getManagerLeads(@PathVariable int managerId) {
        List<Lead> leads = leadService.findByManagerId(managerId);
        return ResponseEntity.ok(leads);
    }

    // Récupérer les leads récents d'un manager
    @GetMapping("/manager/{managerId}/recent")
    public ResponseEntity<List<Lead>> getRecentManagerLeads(
            @PathVariable int managerId,
            @RequestParam(defaultValue = "5") int limit) {
        List<Lead> leads = leadService.findByManagerIdOrderByCreatedAtDesc(managerId, PageRequest.of(0, limit));
        return ResponseEntity.ok(leads);
    }

    // Récupérer le nombre de leads d'un manager
    @GetMapping("/manager/{managerId}/count")
    public ResponseEntity<Map<String, Long>> getManagerLeadsCount(@PathVariable int managerId) {
        long count = leadService.countByManagerId(managerId);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    // Récupérer les leads d'un employé
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Lead>> getEmployeeLeads(@PathVariable int employeeId) {
        List<Lead> leads = leadService.findByEmployeeId(employeeId);
        return ResponseEntity.ok(leads);
    }

    // Récupérer les leads récents d'un employé
    @GetMapping("/employee/{employeeId}/recent")
    public ResponseEntity<List<Lead>> getRecentEmployeeLeads(
            @PathVariable int employeeId,
            @RequestParam(defaultValue = "5") int limit) {
        List<Lead> leads = leadService.findByEmployeeIdOrderByCreatedAtDesc(employeeId, PageRequest.of(0, limit));
        return ResponseEntity.ok(leads);
    }

    // Récupérer le nombre de leads d'un employé
    @GetMapping("/employee/{employeeId}/count")
    public ResponseEntity<Map<String, Long>> getEmployeeLeadsCount(@PathVariable int employeeId) {
        long count = leadService.countByEmployeeId(employeeId);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    // Récupérer les leads d'un client
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Lead>> getCustomerLeads(@PathVariable int customerId) {
        List<Lead> leads = leadService.findByCustomerCustomerId(customerId);
        return ResponseEntity.ok(leads);
    }

    // Récupérer les leads récents d'un client
    @GetMapping("/customer/{customerId}/recent")
    public ResponseEntity<List<Lead>> getRecentCustomerLeads(
            @PathVariable int customerId,
            @RequestParam(defaultValue = "5") int limit) {
        List<Lead> leads = leadService.findByCustomerCustomerIdOrderByCreatedAtDesc(customerId, PageRequest.of(0, limit));
        return ResponseEntity.ok(leads);
    }

    // Récupérer le nombre de leads d'un client
    @GetMapping("/customer/{customerId}/count")
    public ResponseEntity<Map<String, Long>> getCustomerLeadsCount(@PathVariable int customerId) {
        long count = leadService.countByCustomerCustomerId(customerId);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    // Supprimer tous les leads d'un client
    @DeleteMapping("/customer/{customerId}")
    public ResponseEntity<Void> deleteAllCustomerLeads(@PathVariable int customerId) {
        Customer customer = customerService.findByCustomerId(customerId);
        if (customer == null) {
            return ResponseEntity.notFound().build();
        }
        
        leadService.deleteAllByCustomer(customer);
        return ResponseEntity.noContent().build();
    }
}
