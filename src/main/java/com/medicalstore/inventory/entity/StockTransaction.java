package com.medicalstore.inventory.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockTransaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type; // IN, OUT, TRANSFER, ADJUSTMENT

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_bin_id")
    private Bin fromBin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_bin_id")
    private Bin toBin;

    @Column(nullable = false)
    private Integer quantity;

    private String reference; // PO-123, SO-456
    private LocalDateTime transactionDate;

    public enum TransactionType {
        IN, OUT, TRANSFER, ADJUSTMENT
    }
}
