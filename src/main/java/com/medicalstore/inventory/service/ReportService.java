package com.medicalstore.inventory.service;

import java.time.LocalDateTime;
import java.util.Map;

public interface ReportService {
    Map<String, Object> getSalesReport(LocalDateTime start, LocalDateTime end);
    Map<String, Object> getPurchaseReport(LocalDateTime start, LocalDateTime end);
    Map<String, Object> getInventoryValueReport();
}
