package site.easy.to.build.crm.entity.temp;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.security.access.method.P;

import com.opencsv.bean.CsvBindByName;

public class TempBudgetImport {
    @CsvBindByName(column = "customer_email")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String customer_email;

    @CsvBindByName(column = "Budget")
    @Pattern(regexp = "^[+]?\\d+([.,]\\d+)?$", message = "Budget must be a number and positive")
    @NotBlank(message = "Budget is required")
    private String budgetString;

    private BigDecimal budget;

    public TempBudgetImport() {
    }

    public String getCustomer_email() {
        return customer_email;
    }

    public void setCustomer_email(String customer_email) {
        this.customer_email = customer_email;
    }

    public String getBudgetString() {
        return budgetString;
    }

    public void setBudgetString(String budgetString) {
        this.budgetString = budgetString;
        if (budgetString != null && !budgetString.isEmpty()) {
            try {
                String normalized = budgetString.replace(",", ".");
                this.budget = new BigDecimal(normalized);
            } catch (NumberFormatException e) {
                // Leave budget as null
            }
        }
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }
}
