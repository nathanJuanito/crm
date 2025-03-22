package site.easy.to.build.crm.service.budget;

import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Customer;

import java.util.List;

public interface BudgetService {
    
    List<Budget> findAll();
    
    Budget findById(Integer id);
    
    List<Budget> findByCustomer(Customer customer);
    
    List<Budget> findByCustomerId(Integer customerId);
    
    void save(Budget budget);
    
    void deleteById(Integer id);
}
