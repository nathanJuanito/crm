package site.easy.to.build.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import site.easy.to.build.crm.entity.TauxAlerte;

import java.math.BigDecimal;

@Repository
public interface TauxAlerteRepository extends JpaRepository<TauxAlerte, Integer> {
    
    @Query("SELECT t.tauxAlerte FROM TauxAlerte t ORDER BY t.id DESC LIMIT 1")  // Certaines implémentations JPA
    BigDecimal findLatestTauxAlerte();

}
