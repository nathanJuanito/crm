package site.easy.to.build.crm.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.service.customer.CustomerService;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerApiController {
    
    @Autowired
    private CustomerService customerService;
    
    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerService.findAllWithBudgetAndDepenses();
    }
    
    @GetMapping("/{id}")
    public Customer getCustomer(@PathVariable int id) {
        return customerService.findByCustomerId(id);
    }
}