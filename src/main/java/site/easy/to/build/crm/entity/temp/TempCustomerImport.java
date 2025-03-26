package site.easy.to.build.crm.entity.temp;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.opencsv.bean.CsvBindByName;

/**
 * Entité temporaire pour l'importation CSV des données client
 * Cette classe n'est pas une entité JPA mais un simple POJO pour stocker les données CSV
 */
public class TempCustomerImport {
    
    @CsvBindByName(column = "customer_email")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String customer_email;

    @NotBlank(message = "Name is required")
    @CsvBindByName(column = "customer_name")
    private String customer_name;
    
    // Constructeurs, getters et setters
    public TempCustomerImport() {}
    
    public TempCustomerImport(String customer_email, String customer_name) {
        this.customer_email = customer_email;
        this.customer_name = customer_name;
    }
    
    public String getCustomerEmail() {
        return customer_email;
    }
    
    public void setCustomerEmail(String customer_email) {
        this.customer_email = customer_email;
    }
    
    public String getCustomerName() {
        return customer_name;
    }
    
    public void setCustomerName(String customer_name) {
        this.customer_name = customer_name;
    }
    
    @Override
    public String toString() {
        return "TempCustomerImport{" +
                "customer_email='" + customer_email + '\'' +
                ", customer_name='" + customer_name + '\'' +
                '}';
    }
}

