package site.easy.to.build.crm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import site.easy.to.build.crm.entity.Customer;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    public Customer findByCustomerId(int customerId);

    public List<Customer> findByUserId(int userId);

    public Customer findByEmail(String email);

    public List<Customer> findAll();

    public List<Customer> findByUserIdOrderByCreatedAtDesc(int userId, Pageable pageable);

    long countByUserId(int userId);

    // Pour obtenir le solde total d'un client spécifique
    @Query("SELECT COALESCE(SUM(b.montant), 0) FROM Budget b WHERE b.customer.customerId = :customerId")
    BigDecimal getTotalBudgetByCustomerId(@Param("customerId") int customerId);

    // Pour obtenir les soldes de tous les clients en une seule requête
    @Query("SELECT c.customerId, COALESCE(SUM(b.montant), 0) FROM Customer c LEFT JOIN Budget b ON b.customer.customerId = c.customerId GROUP BY c.customerId")
    List<Object[]> getAllCustomersTotalBudget();



}
