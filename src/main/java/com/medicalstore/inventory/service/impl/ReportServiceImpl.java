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
import java.time.format.DateTimeFormatter;

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
    private final com.medicalstore.inventory.repository.CustomerRepository customerRepository;
    private final com.medicalstore.inventory.service.JasperReportService jasperReportService;

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

    @Override
    public java.io.ByteArrayInputStream exportProductsToExcel() throws Exception {
        DateTimeFormatter dateOnlyFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<com.medicalstore.inventory.dto.ReportDto.ProductReport> data = productRepository.findAll().stream()
                .map(p -> com.medicalstore.inventory.dto.ReportDto.ProductReport.builder()
                        .id(p.getId())
                        .productName(p.getProductName())
                        .category(p.getCategory())
                        .quantity(p.getQuantity())
                        .purchasePrice(p.getPurchasePrice())
                        .sellingPrice(p.getSellingPrice())
                        .expiryDate(p.getExpiryDate() != null ? p.getExpiryDate().format(dateOnlyFormatter) : "")
                        .build())
                .collect(java.util.stream.Collectors.toList());
        
        return jasperReportService.exportToExcel("products", data, new HashMap<>());
    }

    @Override
    public java.io.ByteArrayInputStream exportSalesToExcel(LocalDateTime start, LocalDateTime end) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<com.medicalstore.inventory.dto.ReportDto.SaleReport> data = saleRepository.findSalesBetweenDates(start, end).stream()
                .map(s -> com.medicalstore.inventory.dto.ReportDto.SaleReport.builder()
                        .id(s.getId())
                        .saleDate(s.getSaleDate() != null ? s.getSaleDate().format(formatter) : "")
                        .customerName(s.getCustomerName())
                        .totalAmount(s.getTotalAmount())
                        .paymentMethod(s.getPaymentMethod())
                        .status("COMPLETED")
                        .build())
                .collect(java.util.stream.Collectors.toList());

        Map<String, Object> params = new HashMap<>();
        params.put("startDate", start.toString());
        params.put("endDate", end.toString());
        
        return jasperReportService.exportToExcel("sales", data, params);
    }

    @Override
    public java.io.ByteArrayInputStream exportPurchasesToExcel(LocalDateTime start, LocalDateTime end) throws Exception {
        DateTimeFormatter dateOnlyFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<com.medicalstore.inventory.dto.ReportDto.PurchaseReport> data = purchaseRepository.findByPurchaseDateBetween(start.toLocalDate(), end.toLocalDate()).stream()
                .map(p -> com.medicalstore.inventory.dto.ReportDto.PurchaseReport.builder()
                        .id(p.getId())
                        .purchaseDate(p.getPurchaseDate() != null ? p.getPurchaseDate().format(dateOnlyFormatter) : "")
                        .supplierName(p.getSupplier() != null ? p.getSupplier().getSupplierName() : "N/A")
                        .totalAmount(p.getTotalAmount())
                        .referenceNumber(p.getInvoiceNumber())
                        .status("RECEIVED")
                        .build())
                .collect(java.util.stream.Collectors.toList());

        Map<String, Object> params = new HashMap<>();
        params.put("startDate", start.toString());
        params.put("endDate", end.toString());

        return jasperReportService.exportToExcel("purchases", data, params);
    }

    @Override
    public java.io.ByteArrayInputStream exportCustomersToExcel() throws Exception {
        List<com.medicalstore.inventory.dto.ReportDto.CustomerReport> data = customerRepository.findAll().stream()
                .map(c -> com.medicalstore.inventory.dto.ReportDto.CustomerReport.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .phone(c.getPhone())
                        .email(c.getEmail())
                        .totalBalance(c.getTotalBalance())
                        .storeName(c.getStore() != null ? c.getStore().getName() : "Global")
                        .build())
                .collect(java.util.stream.Collectors.toList());

        return jasperReportService.exportToExcel("customers", data, new HashMap<>());
    }
}
