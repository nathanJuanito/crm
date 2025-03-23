package site.easy.to.build.crm.service.customer;

import org.checkerframework.checker.units.qual.C;
import site.easy.to.build.crm.entity.Customer;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface CustomerService {

    public Customer findByCustomerId(int customerId);

    public List<Customer> findByUserId(int userId);

    public Customer findByEmail(String email);

    public List<Customer> findAll();

    public Customer save(Customer customer);

    public void delete(Customer customer);

    public List<Customer> getRecentCustomers(int userId, int limit);

    List<Customer> findAllWithBudgetAndDepenses();

    long countByUserId(int userId);

    // Modifiez ces méthodes pour utiliser int au lieu de Long
    BigDecimal getCustomerTotalBudget(int customerId);
    Map<Integer, BigDecimal> getAllCustomersTotalBudget();


}
