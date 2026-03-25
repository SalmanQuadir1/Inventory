package com.medicalstore.inventory.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CustomerTransactionDto {
    private Long id;
    private Long customerId;
    private String customerName;
    private BigDecimal amount;
    private String transactionType;
    private String reference;
    private LocalDateTime transactionDate;
    private String notes;
}
