package com.medicalstore.inventory.service.impl;

import com.medicalstore.inventory.repository.ProductRepository;
import com.medicalstore.inventory.repository.SaleRepository;
import com.medicalstore.inventory.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;

    @Override
    public Map<String, Object> getDashboardData() {
        Map<String, Object> data = new HashMap<>();

        long totalProducts = productRepository.count();
        int lowStockCount = productRepository.findLowStockProducts().size();
        int expiredCount = productRepository.findExpiredProducts(LocalDate.now()).size();

        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);

        BigDecimal todaySales = saleRepository.findSalesBetweenDates(startOfDay, endOfDay).stream()
                .map(sale -> sale.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (todaySales == null) {
            todaySales = BigDecimal.ZERO;
        }

        data.put("totalProducts", totalProducts);
        data.put("lowStockCount", lowStockCount);
        data.put("expiredCount", expiredCount);
        data.put("todaySales", todaySales);

        return data;
    }
}
