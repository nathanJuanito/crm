package site.easy.to.build.crm.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.service.customer.CustomerService;
import site.easy.to.build.crm.service.depense.DepenseService;
import site.easy.to.build.crm.service.ticket.TicketService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
public class TicketApiController {

    private final TicketService ticketService;
    private final CustomerService customerService;
    private final DepenseService depenseService;

    @Autowired
    public TicketApiController(TicketService ticketService, CustomerService customerService,DepenseService depenseService) {
        this.ticketService = ticketService;
        this.customerService = customerService;
        this.depenseService = depenseService;
    }

    // Récupérer tous les tickets
    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        List<Ticket> tickets = ticketService.findAllWithTotalAmount();
        return ResponseEntity.ok(tickets);
    }

    // Récupérer un ticket par ID
    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable int id) {
        Ticket ticket = ticketService.findByTicketId(id);
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ticket);
    }

    // Créer un nouveau ticket
    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody Ticket ticket) {
        Ticket savedTicket = ticketService.save(ticket);
        return new ResponseEntity<>(savedTicket, HttpStatus.CREATED);
    }

    // Mettre à jour un ticket existant
    @PutMapping("/{ticketId}")
    public ResponseEntity<?> updateTicketMontant(
            @PathVariable("ticketId") int ticketId,
            @RequestParam("montant") String montantStr) {
        
        try {
            // Vérifier si le ticket existe
            Ticket ticket = ticketService.findByTicketId(ticketId);
            if (ticket == null) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Ticket non trouvé avec l'ID: " + ticketId);
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
            
            int ret = depenseService.updateMontant(ticketId,montant);
           
            return new ResponseEntity<>(ret, HttpStatus.OK);
            
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Erreur lors de la mise à jour du montant: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    

    // Supprimer un ticket
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable int id) {
        Ticket ticket = ticketService.findByTicketId(id);
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }
        
        depenseService.deleteByTicketId(ticket.getTicketId());;
        ticketService.delete(ticket);
        
        return ResponseEntity.noContent().build();
    }

    // Récupérer les tickets d'un manager
    @GetMapping("/manager/{managerId}")
    public ResponseEntity<List<Ticket>> getManagerTickets(@PathVariable int managerId) {
        List<Ticket> tickets = ticketService.findManagerTickets(managerId);
        return ResponseEntity.ok(tickets);
    }

    // Récupérer les tickets récents d'un manager
    @GetMapping("/manager/{managerId}/recent")
    public ResponseEntity<List<Ticket>> getRecentManagerTickets(
            @PathVariable int managerId,
            @RequestParam(defaultValue = "5") int limit) {
        List<Ticket> tickets = ticketService.getRecentTickets(managerId, limit);
        return ResponseEntity.ok(tickets);
    }

    // Récupérer le nombre de tickets d'un manager
    @GetMapping("/manager/{managerId}/count")
    public ResponseEntity<Map<String, Long>> getManagerTicketsCount(@PathVariable int managerId) {
        long count = ticketService.countByManagerId(managerId);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    // Récupérer les tickets d'un employé
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Ticket>> getEmployeeTickets(@PathVariable int employeeId) {
        List<Ticket> tickets = ticketService.findEmployeeTickets(employeeId);
        return ResponseEntity.ok(tickets);
    }

    // Récupérer les tickets récents d'un employé
    @GetMapping("/employee/{employeeId}/recent")
    public ResponseEntity<List<Ticket>> getRecentEmployeeTickets(
            @PathVariable int employeeId,
            @RequestParam(defaultValue = "5") int limit) {
        List<Ticket> tickets = ticketService.getRecentEmployeeTickets(employeeId, limit);
        return ResponseEntity.ok(tickets);
    }

    // Récupérer le nombre de tickets d'un employé
    @GetMapping("/employee/{employeeId}/count")
    public ResponseEntity<Map<String, Long>> getEmployeeTicketsCount(@PathVariable int employeeId) {
        long count = ticketService.countByEmployeeId(employeeId);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    // Récupérer les tickets d'un client
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Ticket>> getCustomerTickets(@PathVariable int customerId) {
        List<Ticket> tickets = ticketService.findCustomerTickets(customerId);
        return ResponseEntity.ok(tickets);
    }

    // Récupérer les tickets récents d'un client
    @GetMapping("/customer/{customerId}/recent")
    public ResponseEntity<List<Ticket>> getRecentCustomerTickets(
            @PathVariable int customerId,
            @RequestParam(defaultValue = "5") int limit) {
        List<Ticket> tickets = ticketService.getRecentCustomerTickets(customerId, limit);
        return ResponseEntity.ok(tickets);
    }

    // Récupérer le nombre de tickets d'un client
    @GetMapping("/customer/{customerId}/count")
    public ResponseEntity<Map<String, Long>> getCustomerTicketsCount(@PathVariable int customerId) {
        long count = ticketService.countByCustomerCustomerId(customerId);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    // Supprimer tous les tickets d'un client
    @DeleteMapping("/customer/{customerId}")
    public ResponseEntity<Void> deleteAllCustomerTickets(@PathVariable int customerId) {
        Customer customer = customerService.findByCustomerId(customerId);
        if (customer == null) {
            return ResponseEntity.notFound().build();
        }
        
        ticketService.deleteAllByCustomer(customer);
        return ResponseEntity.noContent().build();
    }
}
