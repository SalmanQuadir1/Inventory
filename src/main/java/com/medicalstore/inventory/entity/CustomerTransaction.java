package com.medicalstore.inventory.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerTransaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    // CREDIT_SALE (increases balance), PAYMENT (decreases balance), REFUND
    @Column(nullable = false)
    private String transactionType; 

    private String reference; // e.g. "Bill #1234"
    
    @Column(nullable = false)
    private LocalDateTime transactionDate;

    private String notes;
}
