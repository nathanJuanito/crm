package site.easy.to.build.crm.controller.api;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import site.easy.to.build.crm.repository.DepenseRepository;
import site.easy.to.build.crm.service.depense.DepenseService;

@RestController
@RequestMapping("/api/total-depense")
public class TotalDepenseApiController {
    
    @Autowired
    private DepenseService depenseService;

    @GetMapping
    public ResponseEntity<Map<String, BigDecimal>> getTotalDepense() {
        BigDecimal depense = depenseService.getTotalDepense();
        Map<String, BigDecimal> response = new HashMap<>();
        response.put("depense", depense);
        return ResponseEntity.ok(response);
    }

}
