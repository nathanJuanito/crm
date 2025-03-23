package site.easy.to.build.crm.service.depense;

import site.easy.to.build.crm.entity.Depense;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.repository.query.Param;

public interface DepenseService {
    
    // Opérations CRUD de base
    Depense save(Depense depense);
    
    Optional<Depense> findById(Integer id);
    
    List<Depense> findAll();
    
    void deleteById(Integer id);
    
    // Méthodes spécifiques
    List<Depense> findByLead(Lead lead);
    
    List<Depense> findByTicket(Ticket ticket);
    
    List<Depense> findByLeadId(Integer leadId);
    
    List<Depense> findByTicketId(Integer ticketId);
    
    // Créer une dépense pour un lead
    Depense createDepenseForLead(Lead lead, BigDecimal montant);
    
    // Créer une dépense pour un ticket
    Depense createDepenseForTicket(Ticket ticket, BigDecimal montant);
    
    // Obtenir le total des dépenses pour un client
    BigDecimal getTotalDepensesByCustomerId(Integer customerId);
    
    // Obtenir le total des dépenses pour tous les clients
    Map<Integer, BigDecimal> getTotalDepensesForAllCustomers();

    int deleteByTicketId(Integer ticketId);

    int updateMontant(Integer ticketId, BigDecimal montant);

    int deleteByLeadId(Integer leadId);

    int updateLeadMontant(Integer leadId, BigDecimal montant);

    BigDecimal getTotalAmountByLeadId(Integer leadId);
}
