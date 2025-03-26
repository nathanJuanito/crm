package site.easy.to.build.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import site.easy.to.build.crm.entity.Depense;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DepenseRepository extends JpaRepository<Depense, Integer> {
   
    // Méthodes existantes...
    
    // Trouver les dépenses par lead
    List<Depense> findByLead(Lead lead);
   
    // Trouver les dépenses par ticket
    List<Depense> findByTicket(Ticket ticket);
   
    // Trouver les dépenses par ID de lead
    List<Depense> findByLeadLeadId(Integer leadId);
   
    // Trouver les dépenses par ID de ticket
    List<Depense> findByTicketTicketId(Integer ticketId);
   
    // Calculer le total des dépenses pour un client spécifique
    @Query("SELECT COALESCE(SUM(d.montant), 0) FROM Depense d " +
        "LEFT JOIN d.ticket t " +
        "LEFT JOIN d.lead l " +
        "LEFT JOIN t.customer tc " +
        "LEFT JOIN l.customer lc " +
        "WHERE tc.customerId = :customerId OR lc.customerId = :customerId")
    BigDecimal getTotalDepensesByCustomerId(@Param("customerId") Integer customerId);

    @Query("SELECT SUM(d.montant) FROM Depense d WHERE d.ticket.ticketId = :ticketId")
    BigDecimal getTotalAmountByTicketId(@Param("ticketId") int ticketId);

    // Calculer le total des dépenses pour tous les clients
    @Query("SELECT c.customerId, SUM(d.montant) FROM Customer c " +
           "LEFT JOIN Lead l ON l.customer.customerId = c.customerId " +
           "LEFT JOIN Ticket t ON t.customer.customerId = c.customerId " +
           "LEFT JOIN Depense d ON d.lead.leadId = l.leadId OR d.ticket.ticketId = t.ticketId " +
           "GROUP BY c.customerId")
    List<Object[]> getTotalDepensesForAllCustomers();
    
    // Méthodes pour les tickets
    @Modifying
    @Transactional
    @Query("DELETE FROM Depense d WHERE d.ticket.ticketId = :ticketId")
    int deleteByTicketId(@Param("ticketId") Integer ticketId);

    @Modifying
    @Transactional
    @Query("UPDATE Depense d SET d.montant = :montant WHERE d.ticket.ticketId = :ticketId")
    int updateMontant(@Param("ticketId") Integer ticketId, @Param("montant") BigDecimal montant);
    
    // Nouvelles méthodes pour les leads
    /**
     * Supprime toutes les dépenses associées à un lead spécifique
     * 
     * @param leadId L'identifiant du lead
     * @return Le nombre de lignes supprimées
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Depense d WHERE d.lead.leadId = :leadId")
    int deleteByLeadId(@Param("leadId") Integer leadId);
    
    /**
     * Met à jour le montant de toutes les dépenses associées à un lead spécifique
     * 
     * @param leadId L'identifiant du lead
     * @param montant Le nouveau montant à définir
     * @return Le nombre de lignes mises à jour
     */
    @Modifying
    @Transactional
    @Query("UPDATE Depense d SET d.montant = :montant WHERE d.lead.leadId = :leadId")
    int updateLeadMontant(@Param("leadId") Integer leadId, @Param("montant") BigDecimal montant);
    
    /**
     * Calcule le montant total des dépenses pour un lead spécifique
     * 
     * @param leadId L'identifiant du lead
     * @return Le montant total des dépenses
     */
    @Query("SELECT SUM(d.montant) FROM Depense d WHERE d.lead.leadId = :leadId")
    BigDecimal getTotalAmountByLeadId(@Param("leadId") Integer leadId);

    @Query("SELECT SUM(d.montant) FROM Depense d")
    BigDecimal getTotalDepense();
}
