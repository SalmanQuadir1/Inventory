package com.medicalstore.inventory.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehousePerformance {
    private String warehouseName;
    private BigDecimal revenue;
    private BigDecimal profit;
    private Long transactionCount;
}
