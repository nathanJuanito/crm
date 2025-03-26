package site.easy.to.build.crm.service.csv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.CustomerLoginInfo;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.entity.temp.ImportError;
import site.easy.to.build.crm.entity.temp.ImportResult;
import site.easy.to.build.crm.entity.temp.TempActivityImport;
import site.easy.to.build.crm.entity.temp.TempBudgetImport;
import site.easy.to.build.crm.entity.temp.TempCustomerImport;
import site.easy.to.build.crm.repository.BudgetRepository;
import site.easy.to.build.crm.repository.CustomerRepository;
import site.easy.to.build.crm.repository.DepenseRepository;
import site.easy.to.build.crm.repository.LeadRepository;
import site.easy.to.build.crm.repository.TicketRepository;
import site.easy.to.build.crm.service.budget.BudgetService;
import site.easy.to.build.crm.service.customer.CustomerLoginInfoService;
import site.easy.to.build.crm.service.depense.DepenseService;
import site.easy.to.build.crm.service.user.UserService;
import site.easy.to.build.crm.util.AuthenticationUtils;
import site.easy.to.build.crm.util.EmailTokenUtils;

import org.springframework.security.core.Authentication;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

@Service
public class CsvImportService {

    @Autowired
    private DepenseService depenseService;
    
    @Autowired
    private UserService userService;

    @Autowired
    private CustomerLoginInfoService customerLoginInfoService;

    @Autowired
    private BudgetService budgetService;

    private final CustomerRepository customerRepository;
    private final TicketRepository ticketRepository;
    private final LeadRepository leadRepository;
    private final Validator validator;

    private final AuthenticationUtils authenticationUtils;
    private static final Logger logger = LoggerFactory.getLogger(CsvImportService.class);

    @Autowired
    public CsvImportService(Validator validator,CustomerRepository customerRepository, 
                           TicketRepository ticketRepository,
                           LeadRepository leadRepository,AuthenticationUtils authenticationUtils) {
        this.validator = validator;
         this.customerRepository = customerRepository;
        this.ticketRepository = ticketRepository;
        this.leadRepository = leadRepository;
        this.authenticationUtils=authenticationUtils;
    }
    /**
     * Parse a CSV file and create a list of temporary entities
     * 
     * @param <T> the type of entity to create
     * @param file the CSV file to parse
     * @param entityClass the class of the entity to create
     * @param result the import result to update with errors
     * @return a list of entities
     */
    public <T> List<T> parseCsvToEntities(MultipartFile file, Class<T> entityClass) {
        List<T> entities = new ArrayList<>();
        
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            // Configure the CSV to Bean mapping strategy
            HeaderColumnNameMappingStrategy<T> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(entityClass);
            
            // Create and configure the CSV to Bean builder
            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .build();
            
            // Parse the CSV file into beans
            entities = csvToBean.parse();
            
            logger.info("Successfully parsed {} entities from file {}", 
                    entities.size(), file.getOriginalFilename());
            
        } catch (Exception e) {
            logger.error("Error parsing CSV file {}: {}", 
                    file.getOriginalFilename(), e.getMessage());
        }
        
        return entities;
    }

    public <T> List<ImportError> validateEntities(List<T> entities, String fileName) {
        List<ImportError> errors = new ArrayList<>();
        
        for (int i = 0; i < entities.size(); i++) {
            T entity = entities.get(i);
            int lineNumber = i+1;
            
            Set<ConstraintViolation<T>> violations = validator.validate(entity);

            for (ConstraintViolation<T> violation : violations) {
                errors.add(new ImportError(
                            fileName,
                            lineNumber,
                            violation.getMessage(),
                            entity.toString()
                    ));
            }
        }
        
        return errors;
    }

    @Transactional
    public int importCustomers(List<TempCustomerImport> customers,Authentication authentication) {
        int count = 0;
        int userId=authenticationUtils.getLoggedInUserId(authentication);
        User loggedInUser=userService.findById(userId);
        
        for (TempCustomerImport tempCustomer : customers) {
            try {
                // Vérifier si le client existe déjà
                Customer existingCustomer = customerRepository.findByEmail(tempCustomer.getCustomerEmail());
                
                if (existingCustomer == null) {
                    // Créer un nouveau client
                    Customer customer = new Customer();
                    customer.setEmail(tempCustomer.getCustomerEmail());
                    customer.setName(tempCustomer.getCustomerName());
                    customer.setCountry("Madagascar");
                    customer.setUser(loggedInUser);
                    customer.setCreatedAt(LocalDateTime.now());

                    CustomerLoginInfo loginInfo = new CustomerLoginInfo();
                    loginInfo.setCustomer(customer);
                    loginInfo.setPasswordSet(null);
                    loginInfo.setEmail(tempCustomer.getCustomerEmail());
                    loginInfo.setToken(EmailTokenUtils.generateToken());

                    // Définir d'autres propriétés selon votre modèle
                    
                    customerRepository.save(customer);
                    customerLoginInfoService.save(loginInfo);

                    logger.info("Created new customer: {}", customer.getEmail());
                } else {
                    // Mettre à jour le client existant
                    existingCustomer.setName(tempCustomer.getCustomerName());
                    // Mettre à jour d'autres propriétés selon votre modèle
                    
                    customerRepository.save(existingCustomer);
                    logger.info("Updated existing customer: {}", existingCustomer.getEmail());
                }
                
                count++;
            } catch (Exception e) {
                logger.error("Error importing customer {}: {}", tempCustomer.getCustomerEmail(), e.getMessage(), e);
                throw new RuntimeException("Failed to import customer: " + e.getMessage(), e);
            }
        }
        
        return count;
    }
    
    /**
     * Importe les activités dans la base de données
     * @param activities Liste des activités à importer
     * @return Nombre d'activités importées
     */
    @Transactional
    public int importActivities(List<TempActivityImport> activities,Authentication authentication) {
        int count = 0;
        int userId=authenticationUtils.getLoggedInUserId(authentication);
        User loggedInUser=userService.findById(userId);
        
        for (TempActivityImport tempActivity : activities) {
            try {
                // Trouver le client associé
                Customer customer = customerRepository.findByEmail(tempActivity.getCustomerEmail());
                
                if (customer == null) {
                    logger.warn("Cannot import activity: Customer with email {} not found", 
                            tempActivity.getCustomerEmail());
                    continue;
                }
                
                // Déterminer le type d'activité et créer l'entité correspondante
                if ("ticket".equalsIgnoreCase(tempActivity.getType())) {
                    Ticket ticket = new Ticket();
                    ticket.setCustomer(customer);
                    ticket.setSubject(tempActivity.getSubjectOrName());
                    ticket.setStatus(tempActivity.getStatus());
                    ticket.setPriority("medium");
                    ticket.setEmployee(loggedInUser);
                    ticket.setCreatedAt(LocalDateTime.now());
                    ticketRepository.save(ticket);

                    // Convertir les dépenses
                    if (tempActivity.getExpense() != null && !tempActivity.getExpense().isEmpty()) {
                        try {
                            String normalizedExpense = tempActivity.getExpense().replace(",", ".");
                            BigDecimal expense = new BigDecimal(normalizedExpense);
                            depenseService.createDepenseForTicket(ticket, expense);
                        } catch (NumberFormatException e) {
                            logger.warn("Could not parse expense: {}", tempActivity.getExpense());
                        }
                    }
                    logger.info("Created new ticket for customer: {}", customer.getEmail());
                    
                } else if ("lead".equalsIgnoreCase(tempActivity.getType())) {
                    Lead lead = new Lead();
                    lead.setCustomer(customer);
                    lead.setName(tempActivity.getSubjectOrName());
                    lead.setStatus(tempActivity.getStatus());
                    lead.setCreatedAt(LocalDateTime.now());
                    leadRepository.save(lead);
                    // Convertir les dépenses
                    if (tempActivity.getExpense() != null && !tempActivity.getExpense().isEmpty()) {
                        try {
                            String normalizedExpense = tempActivity.getExpense().replace(",", ".");
                            BigDecimal expense = new BigDecimal(normalizedExpense);
                            depenseService.createDepenseForLead(lead, expense);
                        } catch (NumberFormatException e) {
                            logger.warn("Could not parse expense: {}", tempActivity.getExpense());
                        }
                    }
                    logger.info("Created new lead for customer: {}", customer.getEmail());
                    
                } else {
                    logger.warn("Unknown activity type: {}", tempActivity.getType());
                    continue;
                }
                
                count++;
            } catch (Exception e) {
                logger.error("Error importing activity for customer {}: {}", 
                        tempActivity.getCustomerEmail(), e.getMessage(), e);
                throw new RuntimeException("Failed to import activity: " + e.getMessage(), e);
            }
        }
        
        return count;
    }

    /**
     * Importe les budgets des clients dans la base de données
     * @param budgets Liste des budgets à importer
     * @param authentication Informations d'authentification de l'utilisateur connecté
     * @return Nombre de budgets importés avec succès
     */
    @Transactional
    public int importBudget(List<TempBudgetImport> budgets) {
        int count = 0;
        for (TempBudgetImport tempBudget : budgets) {
            try {
                // Trouver le client associé
                Customer customer = customerRepository.findByEmail(tempBudget.getCustomer_email());
                
                if (customer == null) {
                    logger.warn("Cannot import budget: Customer with email {} not found",
                            tempBudget.getCustomer_email());
                    continue;
                }
                
                // Mettre à jour le budget du client
                BigDecimal budgetValue = tempBudget.getBudget();
                if (budgetValue != null) {
                    Budget budget = new Budget();
                    budget.setCustomer(customer);
                    budget.setMontant(budgetValue);
                    budgetService.save(budget);

                    logger.info("Updated budget for customer {}: {}", customer.getEmail(), budgetValue);
                    count++;
                } else {
                    logger.warn("Skipping budget import for customer {}: Budget value is null", 
                            tempBudget.getCustomer_email());
                }
                
            } catch (Exception e) {
                logger.error("Error importing budget for customer {}: {}",
                        tempBudget.getCustomer_email(), e.getMessage(), e);
                throw new RuntimeException("Failed to import budget: " + e.getMessage(), e);
            }
        }
        
        return count;
    }

}