package com.medicalstore.inventory.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "short_description")
    private String shortDescription;

    private String manufacturer;

    private String category;

    private String batchNumber;

    private LocalDate expiryDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal purchasePrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal sellingPrice;

    private Integer quantity;

    private Integer reorderLevel;

    // --- Marg ERP Advanced Fields ---
    @Column(precision = 10, scale = 2)
    private BigDecimal mrp;

    private String rackLocation;

    private String hsnCode;

    @Column(precision = 5, scale = 2)
    private BigDecimal gstPercentage;

    @Column(precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    private String packSize; // e.g., "10x10", "100ml"

    @Column(columnDefinition = "TEXT")
    private String specifications;

    private String imagePath;
    // ---------------------------------

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;
}
