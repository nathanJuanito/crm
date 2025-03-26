package site.easy.to.build.crm.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Contract;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.repository.CustomerRepository;
import site.easy.to.build.crm.repository.UserRepository;
import site.easy.to.build.crm.service.depense.DepenseService;
import site.easy.to.build.crm.service.lead.LeadService;
import site.easy.to.build.crm.service.ticket.TicketService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class CsvHelper {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketService ticketService;
    
    @Autowired
    private LeadService leadService;

    @Autowired
    private DepenseService depenseService;


    /**
     * Vérifie si un fichier est au format CSV
     * @param file Le fichier à vérifier
     * @return true si le fichier est au format CSV, false sinon
     */
    public boolean isCSVFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (contentType.equals("text/csv") || contentType.equals("application/vnd.ms-excel"));
    }

    /**
     * Convertit un objet Date en LocalDateTime
     * @param date Date à convertir
     * @return LocalDateTime correspondant
     */
    private LocalDateTime convertToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * Convertit un fichier CSV en liste de clients
     * @param is InputStream du fichier CSV
     * @return Liste des clients importés
     * @throws IOException En cas d'erreur de lecture
     */
    public List<Customer> csvToCustomers(InputStream is) throws IOException {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            
            List<Customer> customers = new ArrayList<>();
            
            for (CSVRecord csvRecord : csvParser) {
                Customer customer = new Customer();
                customer.setName(csvRecord.get("name"));
                customer.setEmail(csvRecord.get("email"));
                customer.setPhone(csvRecord.get("phone"));
                customer.setAddress(csvRecord.get("address"));
                
                if (csvRecord.isMapped("country")) {
                    customer.setCountry(csvRecord.get("country"));
                }
                
                customer.setCreatedAt(LocalDateTime.now());
                customers.add(customer);
            }
            
            return customers;
        }
    }

    /**
     * Convertit un fichier CSV en liste de leads
     * @param is InputStream du fichier CSV
     * @return Liste des leads importés
     * @throws IOException En cas d'erreur de lecture
     */
    public List<Lead> csvToLeads(InputStream is) throws IOException {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            
            List<Lead> leads = new ArrayList<>();
            
            for (CSVRecord csvRecord : csvParser) {
                Lead lead = new Lead();
                lead.setName(csvRecord.get("name"));
                lead.setPhone(csvRecord.get("phone"));
                lead.setStatus(csvRecord.get("status"));
                
                // Récupérer le client associé
                if (csvRecord.isMapped("customer_id")) {
                    try {
                        int customerId = Integer.parseInt(csvRecord.get("customer_id"));
                        customerRepository.findById(customerId).ifPresent(lead::setCustomer);
                    } catch (NumberFormatException e) {
                        // Ignorer si l'ID n'est pas un nombre valide
                    }
                }
                
                lead.setCreatedAt(LocalDateTime.now());
                leads.add(lead);
            }
            
            return leads;
        }
    }

    /**
     * Convertit un fichier CSV en liste de tickets
     * @param is InputStream du fichier CSV
     * @return Liste des tickets importés
     * @throws IOException En cas d'erreur de lecture
     */
        public List<Ticket> csvToTickets(InputStream is) throws IOException {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
        
            List<Ticket> tickets = new ArrayList<>();
        
            for (CSVRecord csvRecord : csvParser) {
                Ticket ticket = new Ticket();
                ticket.setSubject(csvRecord.get("subject"));
                ticket.setDescription(csvRecord.get("description"));
                ticket.setStatus(csvRecord.get("status"));
                ticket.setPriority(csvRecord.get("priority"));
            
                // Récupérer le client associé
                if (csvRecord.isMapped("customer_id")) {
                    try {
                        int customerId = Integer.parseInt(csvRecord.get("customer_id"));
                        Customer customer = customerRepository.getReferenceById(customerId);
                        ticket.setCustomer(customer);
                    } catch (Exception e) {
                        // Ignorer les erreurs
                    }
                }
                
                // Récupérer le manager associé
                if (csvRecord.isMapped("manager_id")) {
                    try {
                        int managerId = Integer.parseInt(csvRecord.get("manager_id"));
                        User manager = userRepository.getReferenceById(managerId);
                        ticket.setManager(manager);
                    } catch (Exception e) {
                        // Ignorer les erreurs
                    }
                }
                
                // Récupérer l'employé associé
                if (csvRecord.isMapped("employee_id")) {
                    try {
                        int employeeId = Integer.parseInt(csvRecord.get("employee_id"));
                        User employee = userRepository.getReferenceById(employeeId);
                        ticket.setEmployee(employee);
                    } catch (Exception e) {
                        // Ignorer les erreurs
                    }
                }
            
                // Date de création
                if (csvRecord.isMapped("created_at")) {
                    try {
                        String dateTimeStr = csvRecord.get("created_at");
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, formatter);
                        ticket.setCreatedAt(dateTime);
                    } catch (Exception e) {
                        ticket.setCreatedAt(LocalDateTime.now());
                    }
                } else {
                    ticket.setCreatedAt(LocalDateTime.now());
                }
                
                tickets.add(ticket);
            }
        
            return tickets;
        }
    }




    /**
     * Convertit un fichier CSV en liste de contrats
     * @param is InputStream du fichier CSV
     * @return Liste des contrats importés
     * @throws IOException En cas d'erreur de lecture
     */
    public List<Contract> csvToContracts(InputStream is) throws IOException {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            
            List<Contract> contracts = new ArrayList<>();
            
            for (CSVRecord csvRecord : csvParser) {
                Contract contract = new Contract();
                contract.setSubject(csvRecord.get("subject"));
                contract.setDescription(csvRecord.get("description"));
                contract.setStatus(csvRecord.get("status"));
                
                // Traiter le montant
                if (csvRecord.isMapped("amount")) {
                    try {
                        BigDecimal amount = new BigDecimal(csvRecord.get("amount"));
                        contract.setAmount(amount);
                    } catch (NumberFormatException e) {
                        // Ignorer si le montant n'est pas un nombre valide
                    }
                }
                
                    if (csvRecord.isMapped("start_date") && !csvRecord.get("start_date").isEmpty()) {
                        // Passer directement la chaîne de caractères
                        contract.setStartDate(csvRecord.get("start_date"));
                    }

                    if (csvRecord.isMapped("end_date") && !csvRecord.get("end_date").isEmpty()) {
                        // Passer directement la chaîne de caractères
                        contract.setEndDate(csvRecord.get("end_date"));
                    }
                // Récupérer le client associé
                if (csvRecord.isMapped("customer_id")) {
                    try {
                        int customerId = Integer.parseInt(csvRecord.get("customer_id"));
                        customerRepository.findById(customerId).ifPresent(contract::setCustomer);
                    } catch (NumberFormatException e) {
                        // Ignorer si l'ID n'est pas un nombre valide
                    }
                }
                
                contract.setCreatedAt(LocalDateTime.now());
                contracts.add(contract);
            }
            
            return contracts;
        }
    }

    /**
     * Exporte une liste de clients vers un fichier CSV
     * @param customers Liste des clients à exporter
     * @param writer Writer pour écrire le fichier CSV
     * @throws IOException En cas d'erreur d'écriture
     */
    public void exportCustomersToCsv(List<Customer> customers, PrintWriter writer) throws IOException {
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader("ID", "Nom", "Email", "Téléphone", "Adresse", "Pays", "Date de création"));
        
        for (Customer customer : customers) {
            csvPrinter.printRecord(
                    customer.getCustomerId(),
                    customer.getName(),
                    customer.getEmail(),
                    customer.getPhone(),
                    customer.getAddress(),
                    customer.getCountry(),
                    customer.getCreatedAt() != null ? customer.getCreatedAt().format(DATE_TIME_FORMATTER) : ""
            );
        }
        
        csvPrinter.flush();
    }
    public void exportToCsv(Customer customer, PrintWriter writer) throws IOException {
        String copyName=customer.getName()+"copy";
        String emailCopy=customer.getEmail()+"copy";
        List<Ticket> tickets = ticketService.findCustomerTickets(customer.getCustomerId());
        List<Lead> leads = leadService.findByCustomerCustomerId(customer.getCustomerId());
        String table="customer";
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader("Table","ID", "Nom", "Email", "Pays", "Date de création"));
                csvPrinter.printRecord(
                    table,
                    customer.getCustomerId(),
                    copyName,
                    emailCopy,
                    customer.getCountry()
            );    
        csvPrinter.flush();
        exportTicketsToCsv(tickets, writer);
        exportLeadsToCsv(leads, writer);
    }

    /**
     * Exporte une liste de leads vers un fichier CSV
     * @param leads Liste des leads à exporter
     * @param writer Writer pour écrire le fichier CSV
     * @throws IOException En cas d'erreur d'écriture
     */
    public void exportLeadsToCsv(List<Lead> leads, PrintWriter writer) throws IOException {
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader("Table","ID", "Nom", "Statut", "Client","depense","Date de création"));
        
        for (Lead lead : leads) {
            String table="lead";
            BigDecimal totalExpense = depenseService.getTotalAmountByLeadId(lead.getLeadId());
            csvPrinter.printRecord(
                    table,
                    lead.getName(),
                    lead.getStatus(),
                    lead.getCustomer() != null ? lead.getCustomer().getCustomerId() : "",
                    totalExpense,
                    lead.getCreatedAt() != null ? lead.getCreatedAt().format(DATE_TIME_FORMATTER) : ""
            );
        }
        
        csvPrinter.flush();

    }

    /**
     * Exporte une liste de tickets vers un fichier CSV
     * @param tickets Liste des tickets à exporter
     * @param writer Writer pour écrire le fichier CSV
     * @throws IOException En cas d'erreur d'écriture
     */
    public void exportTicketsToCsv(List<Ticket> tickets, PrintWriter writer) throws IOException {
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader("Table","ID","Sujet", "Statut", "Priorité", "Client","depense","Date de création"));
        
        for (Ticket ticket : tickets) {
            String table="ticket";
            BigDecimal depense=depenseService.getTotalAmountByTicketId(ticket.getTicketId());
            csvPrinter.printRecord(
                    table,
                    ticket.getTicketId(),
                    ticket.getSubject(),
                    ticket.getStatus(),
                    ticket.getPriority(),
                    ticket.getCustomer() != null ? ticket.getCustomer().getCustomerId() : "",
                    depense,
                    ticket.getCreatedAt() != null ? ticket.getCreatedAt().format(DATE_TIME_FORMATTER) : ""
            );
        }
        
        csvPrinter.flush();
    }

    /**
     * Exporte une liste de contrats vers un fichier CSV
     * @param contracts Liste des contrats à exporter
     * @param writer Writer pour écrire le fichier CSV
     * @throws IOException En cas d'erreur d'écriture
     */
    public void exportContractsToCsv(List<Contract> contracts, PrintWriter writer) throws IOException {
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader("ID", "Sujet", "Description", "Statut", "Montant", "Date de début", "Date de fin", "Client", "Date de création"));
        
        for (Contract contract : contracts) {
            csvPrinter.printRecord(
                    contract.getContractId(),
                    contract.getSubject(),
                    contract.getDescription(),
                    contract.getStatus(),
                    contract.getAmount(),
                    contract.getStartDate() != null ? contract.getStartDate() : "",
                    contract.getEndDate() != null ? contract.getEndDate() : "",
                    contract.getCustomer() != null ? contract.getCustomer().getName() : "",
                    contract.getCreatedAt() != null ? contract.getCreatedAt() : ""
            );
        }
        csvPrinter.flush();
    }
}
