package site.easy.to.build.crm.service.tauxalerte;

import site.easy.to.build.crm.entity.TauxAlerte;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TauxAlerteService {
    
    // Opérations CRUD de base
    TauxAlerte save(TauxAlerte tauxAlerte);
    
    Optional<TauxAlerte> findById(Integer id);
    
    List<TauxAlerte> findAll();
    
    void deleteById(Integer id);
    
    // Méthode spécifique pour récupérer le dernier taux d'alerte configuré
    BigDecimal getLatestTauxAlerte();
}
