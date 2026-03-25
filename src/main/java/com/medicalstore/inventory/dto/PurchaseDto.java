package com.medicalstore.inventory.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class PurchaseDto {
    private Long id;
    
    @NotNull(message = "Supplier is required")
    private Long supplierId;
    
    private String supplierName;
    
    @NotNull(message = "Purchase date is required")
    private LocalDate purchaseDate;
    
    private BigDecimal totalAmount;
    
    @NotEmpty(message = "Purchase items cannot be empty")
    private List<PurchaseItemDto> items = new java.util.ArrayList<>();

    // Marg ERP Extensions
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private BigDecimal discountAmount;
    private BigDecimal gstAmount;
}
