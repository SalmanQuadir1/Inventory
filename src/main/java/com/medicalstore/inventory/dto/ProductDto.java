package com.medicalstore.inventory.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;

    @NotBlank(message = "Product name is required")
    private String productName;

    private String shortDescription;
    private String manufacturer;
    private String category;
    private String batchNumber;

    @NotNull(message = "Expiry date is required")
    private LocalDate expiryDate;

    @NotNull(message = "Purchase price is required")
    @Min(value = 0, message = "Price cannot be negative")
    private BigDecimal purchasePrice;

    @NotNull(message = "Selling price is required")
    @Min(value = 0, message = "Price cannot be negative")
    private BigDecimal sellingPrice;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    @NotNull(message = "Reorder level is required")
    @Min(value = 0, message = "Reorder level cannot be negative")
    private Integer reorderLevel;

    private Long supplierId;
    private String supplierName;
    private SupplierDto supplier;

    // ERP Extensions
    private BigDecimal mrp;
    private String rackLocation;
    private String hsnCode;
    private BigDecimal gstPercentage;
    private BigDecimal discountPercentage;
    private String packSize;
    private String specifications;

    private String imagePath;
    private MultipartFile imageFile;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
