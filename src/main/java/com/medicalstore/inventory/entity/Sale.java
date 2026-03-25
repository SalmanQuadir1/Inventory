package com.medicalstore.inventory.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sale extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime saleDate;

    @Column(precision = 12, scale = 2)
    private BigDecimal totalAmount;

    private String paymentMethod; // CASH, CARD, UPI

    // --- Marg ERP Advanced Fields ---
    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "discount_amount", precision = 12, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "gst_amount", precision = 12, scale = 2)
    private BigDecimal gstAmount;
    // ---------------------------------

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private Warehouse store;

    @Builder.Default
    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleItem> items = new ArrayList<>();

    public void addItem(SaleItem item) {
        if (items == null)
            items = new ArrayList<>();
        items.add(item);
        item.setSale(this);
    }

    public void removeItem(SaleItem item) {
        if (items != null) {
            items.remove(item);
            item.setSale(null);
        }
    }
}
