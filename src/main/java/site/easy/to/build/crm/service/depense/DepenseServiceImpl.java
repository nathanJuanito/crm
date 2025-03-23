package site.easy.to.build.crm.service.depense;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.entity.Depense;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.repository.DepenseRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DepenseServiceImpl implements DepenseService {

    @Autowired
    private DepenseRepository depenseRepository;

    @Override
    public Depense save(Depense depense) {
        return depenseRepository.save(depense);
    }

    @Override
    public Optional<Depense> findById(Integer id) {
        return depenseRepository.findById(id);
    }

    @Override
    public List<Depense> findAll() {
        return depenseRepository.findAll();
    }

    @Override
    public void deleteById(Integer id) {
        depenseRepository.deleteById(id);
    }

    @Override
    public List<Depense> findByLead(Lead lead) {
        return depenseRepository.findByLead(lead);
    }

    @Override
    public List<Depense> findByTicket(Ticket ticket) {
        return depenseRepository.findByTicket(ticket);
    }

    @Override
    public List<Depense> findByLeadId(Integer leadId) {
        return depenseRepository.findByLeadLeadId(leadId);
    }

    @Override
    public List<Depense> findByTicketId(Integer ticketId) {
        return depenseRepository.findByTicketTicketId(ticketId);
    }

    @Override
    public Depense createDepenseForLead(Lead lead, BigDecimal montant) {
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
            return null; // Ne pas créer de dépense si le montant est nul ou négatif
        }
        
        Depense depense = new Depense();
        depense.setLead(lead);
        depense.setTicket(null);
        depense.setMontant(montant);
        
        return depenseRepository.save(depense);
    }

    @Override
    public Depense createDepenseForTicket(Ticket ticket, BigDecimal montant) {
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
            return null; // Ne pas créer de dépense si le montant est nul ou négatif
        }
        
        Depense depense = new Depense();
        depense.setLead(null);
        depense.setTicket(ticket);
        depense.setMontant(montant);
        
        return depenseRepository.save(depense);
    }

    @Override
    public BigDecimal getTotalDepensesByCustomerId(Integer customerId) {
        BigDecimal total = depenseRepository.getTotalDepensesByCustomerId(customerId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public Map<Integer, BigDecimal> getTotalDepensesForAllCustomers() {
        Map<Integer, BigDecimal> result = new HashMap<>();
        List<Object[]> data = depenseRepository.getTotalDepensesForAllCustomers();
        
        if (data != null) {
            for (Object[] row : data) {
                Integer customerId = (Integer) row[0];
                BigDecimal total = (BigDecimal) row[1];
                result.put(customerId, total != null ? total : BigDecimal.ZERO);
            }
        }
        
        return result;
    }

    @Override
    public int deleteByTicketId(Integer ticketId) {
        return depenseRepository.deleteByTicketId(ticketId);
    }

    @Override
    public int updateMontant(Integer ticketId, BigDecimal montant) {
        return depenseRepository.updateMontant(ticketId, montant);
    }

    @Override
    public int deleteByLeadId(Integer leadId) {
        return depenseRepository.deleteByLeadId(leadId);
    }

    @Override
    public int updateLeadMontant(Integer leadId, BigDecimal montant) {
        return depenseRepository.updateLeadMontant(leadId, montant);
    }

    @Override
    public BigDecimal getTotalAmountByLeadId(Integer leadId) {
        return depenseRepository.getTotalAmountByLeadId(leadId);
    }

}
