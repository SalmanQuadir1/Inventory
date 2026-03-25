package com.medicalstore.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SaleDto {
    private Long id;
    private LocalDateTime saleDate;
    private BigDecimal totalAmount;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    @NotEmpty(message = "Sale items cannot be empty")
    private List<SaleItemDto> items = new java.util.ArrayList<>();

    // Marg ERP Extensions
    private Long customerId;
    private String customerName;
    private String phoneNumber;
    private String remarks;
    private BigDecimal discountAmount;
    private BigDecimal gstAmount;
}
