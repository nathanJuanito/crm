package site.easy.to.build.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Customer;

import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Integer> {
    
    List<Budget> findByCustomer(Customer customer);
    
    @Query("SELECT b FROM Budget b WHERE b.customer.id = :customerId")
    List<Budget> findByCustomerId(@Param("customerId") Integer customerId);
}
