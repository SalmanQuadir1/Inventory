package com.medicalstore.inventory.service.impl;

import com.medicalstore.inventory.entity.Purchase;
import com.medicalstore.inventory.entity.Sale;
import com.medicalstore.inventory.repository.ProductRepository;
import com.medicalstore.inventory.repository.PurchaseRepository;
import com.medicalstore.inventory.repository.SaleRepository;
import com.medicalstore.inventory.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final SaleRepository saleRepository;
    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;

    @Override
    public Map<String, Object> getSalesReport(LocalDateTime start, LocalDateTime end) {
        List<Sale> sales = saleRepository.findSalesBetweenDates(start, end);
        BigDecimal totalRevenue = sales.stream()
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> report = new HashMap<>();
        report.put("transactions", sales);
        report.put("totalRevenue", totalRevenue);
        return report;
    }

    @Override
    public Map<String, Object> getPurchaseReport(LocalDateTime start, LocalDateTime end) {
        List<Purchase> purchases = purchaseRepository.findByPurchaseDateBetween(start.toLocalDate(), end.toLocalDate());

        BigDecimal totalExpense = purchases.stream()
                .map(Purchase::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> report = new HashMap<>();
        report.put("transactions", purchases);
        report.put("totalExpense", totalExpense);
        return report;
    }

    @Override
    public Map<String, Object> getInventoryValueReport() {
        BigDecimal totalValue = productRepository.findAll().stream()
                .map(p -> (p.getPurchasePrice() != null ? p.getPurchasePrice() : BigDecimal.ZERO)
                        .multiply(new BigDecimal(p.getQuantity() != null ? p.getQuantity() : 0)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> report = new HashMap<>();
        report.put("totalInventoryValue", totalValue != null ? totalValue : BigDecimal.ZERO);
        return report;
    }
}
