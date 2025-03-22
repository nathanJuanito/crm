package site.easy.to.build.crm.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "montant", precision = 15, scale = 2)
    private BigDecimal montant;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // Constructeurs
    public Budget() {
    }

    public Budget(BigDecimal montant, Customer customer) {
        this.montant = montant;
        this.customer = customer;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        return "Budget{" +
                "id=" + id +
                ", montant=" + montant +
                ", customer=" + (customer != null ? customer.getCustomerId() : "null") +
                '}';
    }
}
