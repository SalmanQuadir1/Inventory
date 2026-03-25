package com.medicalstore.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class ReportDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductReport {
        private Long id;
        private String productName;
        private String category;
        private Integer quantity;
        private BigDecimal purchasePrice;
        private BigDecimal sellingPrice;
        private String expiryDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaleReport {
        private Long id;
        private String saleDate;
        private String customerName;
        private BigDecimal totalAmount;
        private String paymentMethod;
        private String status;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseReport {
        private Long id;
        private String purchaseDate;
        private String supplierName;
        private BigDecimal totalAmount;
        private String referenceNumber;
        private String status;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerReport {
        private Long id;
        private String name;
        private String phone;
        private String email;
        private BigDecimal totalBalance;
        private String storeName;
    }
}
