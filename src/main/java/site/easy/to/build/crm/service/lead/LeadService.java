package site.easy.to.build.crm.service.lead;

import org.springframework.data.domain.Pageable;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Lead;

import java.util.List;

public interface LeadService {
    Lead findByLeadId(int id);
    
    Lead save(Lead lead);
    
    void delete(Lead lead);
    
    List<Lead> findAll();
    
    // Ajoutez ces méthodes
    List<Lead> findByManagerId(int managerId);
    
    List<Lead> findByEmployeeId(int employeeId);
    
    Lead findByMeetingId(String meetingId);
    
    List<Lead> findByEmployeeIdOrderByCreatedAtDesc(int employeeId, Pageable pageable);
    
    List<Lead> findByManagerIdOrderByCreatedAtDesc(int managerId, Pageable pageable);
    
    List<Lead> findByCustomerCustomerId(int customerId);
    
    List<Lead> findByCustomerCustomerIdOrderByCreatedAtDesc(int customerId, Pageable pageable);
    
    long countByEmployeeId(int employeeId);
    
    long countByManagerId(int managerId);
    
    long countByCustomerCustomerId(int customerId);
    
    void deleteAllByCustomer(Customer customer);

     List<Lead> findAssignedLeads(int employeeId);
    List<Lead> findCreatedLeads(int employeeId);
    List<Lead> getCustomerLeads(Integer customerId);
    List<Lead> getRecentCustomerLeads(int customerId, int limit);
    long countByCustomerId(int customerId);
    List<Lead> getRecentLeadsByEmployee(int employeeId, int limit);

    List<Lead> findAllWithTotalAmount();
}
