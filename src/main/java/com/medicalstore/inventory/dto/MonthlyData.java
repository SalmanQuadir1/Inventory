package com.medicalstore.inventory.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyData {
    private String month;
    private BigDecimal revenue;
    private BigDecimal profit;

    // Custom constructor for JPA aggregate results (Object month)
    public MonthlyData(Object month, BigDecimal revenue, BigDecimal profit) {
        this.month = (month != null) ? String.valueOf(month) : null;
        this.revenue = (revenue != null) ? revenue : BigDecimal.ZERO;
        this.profit = (profit != null) ? profit : BigDecimal.ZERO;
    }
}
