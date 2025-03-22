package site.easy.to.build.crm.service.budget;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.repository.BudgetRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BudgetServiceImpl implements BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;
    
    @Override
    public List<Budget> findAll() {
        return budgetRepository.findAll();
    }
    
    @Override
    public Budget findById(Integer id) {
        Optional<Budget> result = budgetRepository.findById(id);
        
        Budget budget = null;
        
        if (result.isPresent()) {
            budget = result.get();
        } else {
            throw new RuntimeException("Did not find budget id - " + id);
        }
        
        return budget;
    }
    
    @Override
    public List<Budget> findByCustomer(Customer customer) {
        return budgetRepository.findByCustomer(customer);
    }
    
    @Override
    public List<Budget> findByCustomerId(Integer customerId) {
        return budgetRepository.findByCustomerId(customerId);
    }
    
    @Override
    public void save(Budget budget) {
        budgetRepository.save(budget);
    }
    
    @Override
    public void deleteById(Integer id) {
        budgetRepository.deleteById(id);
    }
}
