package site.easy.to.build.crm.service.tauxalerte;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.entity.TauxAlerte;
import site.easy.to.build.crm.repository.TauxAlerteRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class TauxAlerteServiceImpl implements TauxAlerteService {

    @Autowired
    private TauxAlerteRepository tauxAlerteRepository;

    @Override
    public TauxAlerte save(TauxAlerte tauxAlerte) {
        return tauxAlerteRepository.save(tauxAlerte);
    }

    @Override
    public Optional<TauxAlerte> findById(Integer id) {
        return tauxAlerteRepository.findById(id);
    }

    @Override
    public List<TauxAlerte> findAll() {
        return tauxAlerteRepository.findAll();
    }

    @Override
    public void deleteById(Integer id) {
        tauxAlerteRepository.deleteById(id);
    }

    @Override
    public BigDecimal getLatestTauxAlerte() {
        BigDecimal tauxAlerte = tauxAlerteRepository.findLatestTauxAlerte();
        // Si aucun taux d'alerte n'est configuré, on retourne une valeur par défaut (80%)
        return tauxAlerte != null ? tauxAlerte : new BigDecimal("0.80");
    }
}
