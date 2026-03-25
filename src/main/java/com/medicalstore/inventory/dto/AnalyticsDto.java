package com.medicalstore.inventory.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDto {
    private BigDecimal totalRevenue;
    private BigDecimal totalProfit;
    private BigDecimal inventoryValue;
    private double salesGrowth; // Percentage compared to last month
    
    private List<MonthlyData> salesTrend;
    private List<ProductPerformance> topProducts;
    private List<WarehousePerformance> warehouseStats;

    @Data
    @Builder
    public static class MonthlyData {
        private String month;
        private BigDecimal revenue;
        private BigDecimal profit;
    }

    @Data
    @Builder
    public static class ProductPerformance {
        private String productName;
        private Long quantitySold;
        private BigDecimal revenue;
        private BigDecimal profit;
    }

    @Data
    @Builder
    public static class WarehousePerformance {
        private String warehouseName;
        private BigDecimal revenue;
        private BigDecimal profit;
        private long transactionCount;
    }
}
