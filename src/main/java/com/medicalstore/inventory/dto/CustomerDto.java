package com.medicalstore.inventory.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CustomerDto {
    private Long id;
    
    @NotBlank(message = "Customer name is required")
    private String name;
    
    private String phone;
    private String email;
    private String address;
    private BigDecimal totalBalance;
    private Long storeId;
    private String storeName;
    private LocalDateTime createdAt;
}
