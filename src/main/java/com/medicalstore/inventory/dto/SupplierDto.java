package com.medicalstore.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDto {
    private Long id;
    
    @NotBlank(message = "Supplier name is required")
    private String supplierName;
    
    private String contactPerson;
    private String phone;
    private String email;
    private String address;

    // Marg ERP Extensions
    private String gstin;
    private String dlNumber;
    private String bankName;
    private String accountNumber;
    private String ifscCode;
    private String state;
}
