package site.easy.to.build.crm.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "taux_alerte")
public class TauxAlerte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "taux_alerte", precision = 15, scale = 2)
    private BigDecimal tauxAlerte;

    // Constructeurs
    public TauxAlerte() {
    }

    public TauxAlerte(BigDecimal tauxAlerte) {
        this.tauxAlerte = tauxAlerte;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getTauxAlerte() {
        return tauxAlerte;
    }

    public void setTauxAlerte(BigDecimal tauxAlerte) {
        this.tauxAlerte = tauxAlerte;
    }

    @Override
    public String toString() {
        return "TauxAlerte{" +
                "id=" + id +
                ", tauxAlerte=" + tauxAlerte +
                '}';
    }
}
