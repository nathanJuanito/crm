package site.easy.to.build.crm.service.lead;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.repository.DepenseRepository;
import site.easy.to.build.crm.repository.LeadRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class LeadServiceImpl implements LeadService {

    @Autowired
    private DepenseRepository depenseRepository;

    @Autowired
    private LeadRepository leadRepository;

    @Override
    public Lead findByLeadId(int id) {
        return leadRepository.findByLeadId(id);
    }

    @Override
    public Lead save(Lead lead) {
        return leadRepository.save(lead);
    }

    @Override
    public void delete(Lead lead) {
        leadRepository.delete(lead);
    }

    @Override
    public List<Lead> findAll() {
        return leadRepository.findAll();
    }

    // Implémentez les nouvelles méthodes
    @Override
    public List<Lead> findByManagerId(int managerId) {
        return leadRepository.findByManagerId(managerId);
    }

    @Override
    public List<Lead> findByEmployeeId(int employeeId) {
        return leadRepository.findByEmployeeId(employeeId);
    }

    @Override
    public Lead findByMeetingId(String meetingId) {
        return leadRepository.findByMeetingId(meetingId);
    }

    @Override
    public List<Lead> findByEmployeeIdOrderByCreatedAtDesc(int employeeId, Pageable pageable) {
        return leadRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId, pageable);
    }

    @Override
    public List<Lead> findByManagerIdOrderByCreatedAtDesc(int managerId, Pageable pageable) {
        return leadRepository.findByManagerIdOrderByCreatedAtDesc(managerId, pageable);
    }

    @Override
    public List<Lead> findByCustomerCustomerId(int customerId) {
        return leadRepository.findByCustomerCustomerId(customerId);
    }

    @Override
    public List<Lead> findByCustomerCustomerIdOrderByCreatedAtDesc(int customerId, Pageable pageable) {
        return leadRepository.findByCustomerCustomerIdOrderByCreatedAtDesc(customerId, pageable);
    }

    @Override
    public long countByEmployeeId(int employeeId) {
        return leadRepository.countByEmployeeId(employeeId);
    }

    @Override
    public long countByManagerId(int managerId) {
        return leadRepository.countByManagerId(managerId);
    }

    @Override
    public long countByCustomerCustomerId(int customerId) {
        return leadRepository.countByCustomerCustomerId(customerId);
    }

    @Override
    public void deleteAllByCustomer(Customer customer) {
        leadRepository.deleteAllByCustomer(customer);
    }

    @Override
    public List<Lead> findAssignedLeads(int employeeId) {
        // Cette méthode devrait retourner les leads assignés à un employé
        // Si cette méthode n'existe pas dans le repository, vous devez l'implémenter
        // ou utiliser une méthode existante qui fait la même chose
        return leadRepository.findByEmployeeId(employeeId);
    }

    @Override
    public List<Lead> findCreatedLeads(int employeeId) {
        // Cette méthode devrait retourner les leads créés par un employé
        // Si cette méthode n'existe pas dans le repository, vous devez l'implémenter
        // ou utiliser une méthode existante qui fait la même chose
        // Par exemple, si vous avez un champ createdBy dans votre entité Lead:
        // return leadRepository.findByCreatedById(employeeId);
        
        // Sinon, vous pouvez utiliser une requête personnalisée:
        // Vous devrez ajouter cette méthode à votre repository
        return leadRepository.findByManagerId(employeeId); // Temporaire, à remplacer
    }

    @Override
    public List<Lead> getCustomerLeads(Integer customerId) {
        // Cette méthode devrait retourner les leads associés à un client
        return leadRepository.findByCustomerCustomerId(customerId);
    }

    @Override
    public List<Lead> getRecentCustomerLeads(int customerId, int limit) {
        // Cette méthode devrait retourner les leads récents associés à un client
        return leadRepository.findByCustomerCustomerIdOrderByCreatedAtDesc(
            customerId, PageRequest.of(0, limit));
    }

    @Override
    public long countByCustomerId(int customerId) {
        // Cette méthode devrait compter les leads associés à un client
        return leadRepository.countByCustomerCustomerId(customerId);
    }

    @Override
    public List<Lead> getRecentLeadsByEmployee(int employeeId, int limit) {
        // Cette méthode devrait retourner les leads récents associés à un employé
        return leadRepository.findByEmployeeIdOrderByCreatedAtDesc(
            employeeId, PageRequest.of(0, limit));
    }

    @Override
    public List<Lead> findAllWithTotalAmount() {
        List<Lead> leads = leadRepository.findAll();
        
        // Pour chaque ticket, calculer le montant total des dépenses
        for (Lead lead : leads) {
            BigDecimal totalAmount = depenseRepository.getTotalAmountByLeadId(lead.getLeadId());
            lead.setMontant(totalAmount != null ? totalAmount : BigDecimal.ZERO);
        }
        
        return leads;
    }
}
