package site.easy.to.build.crm.service.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.repository.CustomerRepository;
import site.easy.to.build.crm.repository.DepenseRepository;
import site.easy.to.build.crm.entity.Customer;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final DepenseRepository depenseRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, DepenseRepository depenseRepository) {
        this.customerRepository = customerRepository;
        this.depenseRepository = depenseRepository;
    }

    @Override
    public Customer findByCustomerId(int customerId) {
        return customerRepository.findByCustomerId(customerId);
    }

    @Override
    public Customer findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    @Override
    public List<Customer> findByUserId(int userId) {
        return customerRepository.findByUserId(userId);
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public void delete(Customer customer) {
        customerRepository.delete(customer);
    }

    @Override
    public List<Customer> getRecentCustomers(int userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return customerRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Override
    public long countByUserId(int userId) {
        return customerRepository.countByUserId(userId);
    }

    @Override
    public BigDecimal getCustomerTotalBudget(int customerId) {
        BigDecimal total = customerRepository.getTotalBudgetByCustomerId(customerId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public Map<Integer, BigDecimal> getAllCustomersTotalBudget() {
        List<Object[]> results = customerRepository.getAllCustomersTotalBudget();
        Map<Integer, BigDecimal> totalBudgets = new HashMap<>();
       
        for (Object[] result : results) {
            Integer customerId = (Integer) result[0];
            BigDecimal total = (BigDecimal) result[1];
            totalBudgets.put(customerId, total != null ? total : BigDecimal.ZERO);
        }
       
        return totalBudgets;
    }

    @Override
    public List<Customer> findAllWithBudgetAndDepenses() {
        List<Customer> customers = customerRepository.findAll();
       
        // Pour chaque client, calculer et définir le budget total
        for (Customer customer : customers) {
            BigDecimal totalBudget = getCustomerTotalBudget(customer.getCustomerId());
            BigDecimal totalDepenses = depenseRepository.getTotalDepensesByCustomerId(customer.getCustomerId());
            customer.setBudget(totalBudget);
            customer.setDepenses(totalDepenses);
        }
        return customers;
    }
}
