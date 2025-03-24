CREATE TABLE budgets (
    id INT AUTO_INCREMENT,
    montant DECIMAL(15,2),
    customer_id INT unsigned NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY(customer_id) REFERENCES customer(customer_id)
)ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE taux_alerte(
   id INT AUTO_INCREMENT,
   taux_alerte DECIMAL(15,2) ,
   PRIMARY KEY(id)
)ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE depenses(
   id INT AUTO_INCREMENT,
   montant DECIMAL(15,2)  ,
   lead_id INT unsigned,
   ticket_id INT unsigned,
   PRIMARY KEY(id),
   FOREIGN KEY(lead_id) REFERENCES trigger_lead(lead_id),
   FOREIGN KEY(ticket_id) REFERENCES trigger_ticket(ticket_id)
);
