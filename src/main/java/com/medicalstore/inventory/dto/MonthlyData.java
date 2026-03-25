package com.medicalstore.inventory.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyData {
    private Object month;
    private BigDecimal revenue;
    private BigDecimal profit;

    public String getMonthLabel() {
        return String.valueOf(month);
    }
}
