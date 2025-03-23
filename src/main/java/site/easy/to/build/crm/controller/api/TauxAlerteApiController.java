package site.easy.to.build.crm.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.easy.to.build.crm.entity.TauxAlerte;
import site.easy.to.build.crm.service.tauxalerte.TauxAlerteService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/taux-alerte")
public class TauxAlerteApiController {

    private final TauxAlerteService tauxAlerteService;

    @Autowired
    public TauxAlerteApiController(TauxAlerteService tauxAlerteService) {
        this.tauxAlerteService = tauxAlerteService;
    }

    // Récupérer le taux d'alerte actuel
    @GetMapping
    public ResponseEntity<Map<String, BigDecimal>> getTauxAlerte() {
        BigDecimal tauxAlerte = tauxAlerteService.getLatestTauxAlerte();
        Map<String, BigDecimal> response = new HashMap<>();
        response.put("taux", tauxAlerte);
        return ResponseEntity.ok(response);
    }

    // Mettre à jour le taux d'alerte
    @PostMapping
    public ResponseEntity<?> updateTauxAlerte(@RequestBody Map<String, Object> payload) {
        try {
            // Vérifier si le payload contient le taux
            if (!payload.containsKey("taux")) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Le paramètre 'taux' est requis");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            // Convertir et valider le taux
            BigDecimal taux;
            try {
                if (payload.get("taux") instanceof Number) {
                    taux = new BigDecimal(((Number) payload.get("taux")).toString());
                } else {
                    taux = new BigDecimal(payload.get("taux").toString());
                }
            } catch (NumberFormatException e) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Le taux doit être un nombre valide");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            // Valider la plage du taux (entre 1 et 100)
            if (taux.compareTo(BigDecimal.ONE) < 0 || taux.compareTo(new BigDecimal("100")) > 0) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Le taux doit être compris entre 1 et 100");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            // Créer et sauvegarder un nouveau taux d'alerte
            TauxAlerte nouveauTauxAlerte = new TauxAlerte();
            nouveauTauxAlerte.setTauxAlerte(taux);
            tauxAlerteService.save(nouveauTauxAlerte);

            // Réponse de succès
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("success", true);
            successResponse.put("message", "Taux d'alerte mis à jour avec succès");
            successResponse.put("taux", taux);
            return new ResponseEntity<>(successResponse, HttpStatus.OK);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erreur lors de la mise à jour du taux d'alerte: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
