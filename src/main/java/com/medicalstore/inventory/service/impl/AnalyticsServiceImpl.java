package com.medicalstore.inventory.service.impl;

import com.medicalstore.inventory.dto.AnalyticsDto;
import com.medicalstore.inventory.repository.ProductRepository;
import com.medicalstore.inventory.repository.SaleRepository;
import com.medicalstore.inventory.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public AnalyticsDto getManagementDashboardData() {
        BigDecimal totalRevenue = saleRepository.getTotalRevenue();
        BigDecimal totalProfit = saleRepository.getTotalProfit();
        
        // Inventory valuation
        BigDecimal inventoryValue = productRepository.findAll().stream()
                .map(p -> (p.getPurchasePrice() != null ? p.getPurchasePrice() : BigDecimal.ZERO)
                        .multiply(new BigDecimal(p.getQuantity() != null ? p.getQuantity() : 0)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<AnalyticsDto.MonthlyData> monthlyTrend = saleRepository.getMonthlyAnalytics();
        
        // Calculate growth (Simplified: Current Month vs Previous available month)
        double growth = 0;
        if (monthlyTrend.size() >= 2) {
            BigDecimal current = monthlyTrend.get(0).getRevenue();
            BigDecimal previous = monthlyTrend.get(1).getRevenue();
            if (current != null && previous != null && previous.compareTo(BigDecimal.ZERO) > 0) {
                growth = current.subtract(previous)
                        .divide(previous, 4, java.math.RoundingMode.HALF_UP)
                        .multiply(new BigDecimal(100))
                        .doubleValue();
            }
        }

        List<AnalyticsDto.ProductPerformance> topProducts = saleRepository.getTopSellingProductsNative().stream()
                .map(obj -> AnalyticsDto.ProductPerformance.builder()
                        .productName((String) obj[0])
                        .quantitySold(((Number) obj[1]).longValue())
                        .revenue(new BigDecimal(obj[2].toString()))
                        .profit(new BigDecimal(obj[3].toString()))
                        .build())
                .collect(Collectors.toList());

        return AnalyticsDto.builder()
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .totalProfit(totalProfit != null ? totalProfit : BigDecimal.ZERO)
                .inventoryValue(inventoryValue)
                .salesGrowth(growth)
                .salesTrend(monthlyTrend)
                .topProducts(topProducts)
                .warehouseStats(saleRepository.getWarehousePerformance())
                .build();
    }
}
