package com.medicalstore.inventory.controller;

import com.medicalstore.inventory.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.io.ByteArrayInputStream;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
@SuppressWarnings("null")
public class ReportController {

    private final ProductService productService;
    private final com.medicalstore.inventory.service.ReportService reportService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("inventoryValue", reportService.getInventoryValueReport().get("totalInventoryValue"));
        return "reports/index";
    }

    @GetMapping("/low-stock")
    public String lowStock(Model model) {
        model.addAttribute("products", productService.getLowStockProducts());
        model.addAttribute("reportTitle", "Low Stock Report");
        return "reports/product-report";
    }

    @GetMapping("/expired")
    public String expired(Model model) {
        model.addAttribute("products", productService.getExpiredProducts());
        model.addAttribute("reportTitle", "Expired Products Report");
        return "reports/product-report";
    }

    @GetMapping("/sales")
    public String salesReport(
            @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) @org.springframework.web.bind.annotation.RequestParam(required = false) java.time.LocalDateTime start,
            @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) @org.springframework.web.bind.annotation.RequestParam(required = false) java.time.LocalDateTime end,
            Model model) {
        if (start == null)
            start = java.time.LocalDateTime.now().minusMonths(1);
        if (end == null)
            end = java.time.LocalDateTime.now();

        model.addAttribute("reportData", reportService.getSalesReport(start, end));
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        return "reports/sales";
    }

    @GetMapping("/purchases")
    public String purchaseReport(
            @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) @org.springframework.web.bind.annotation.RequestParam(required = false) java.time.LocalDateTime start,
            @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) @org.springframework.web.bind.annotation.RequestParam(required = false) java.time.LocalDateTime end,
            Model model) {
        if (start == null)
            start = java.time.LocalDateTime.now().minusMonths(1);
        if (end == null)
            end = java.time.LocalDateTime.now();

        model.addAttribute("reportData", reportService.getPurchaseReport(start, end));
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        return "reports/purchases";
    }

    @GetMapping("/export/products")
    public ResponseEntity<Resource> exportProducts() throws Exception {
        ByteArrayInputStream bis = reportService.exportProductsToExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=products_report.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(bis));
    }

    @GetMapping("/export/sales")
    public ResponseEntity<Resource> exportSales(
            @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) @org.springframework.web.bind.annotation.RequestParam(required = false) java.time.LocalDateTime start,
            @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) @org.springframework.web.bind.annotation.RequestParam(required = false) java.time.LocalDateTime end) throws Exception {
        if (start == null) start = java.time.LocalDateTime.now().minusMonths(1);
        if (end == null) end = java.time.LocalDateTime.now();

        ByteArrayInputStream bis = reportService.exportSalesToExcel(start, end);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=sales_report.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(bis));
    }

    @GetMapping("/export/purchases")
    public ResponseEntity<Resource> exportPurchases(
            @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) @org.springframework.web.bind.annotation.RequestParam(required = false) java.time.LocalDateTime start,
            @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) @org.springframework.web.bind.annotation.RequestParam(required = false) java.time.LocalDateTime end) throws Exception {
        if (start == null) start = java.time.LocalDateTime.now().minusMonths(1);
        if (end == null) end = java.time.LocalDateTime.now();

        ByteArrayInputStream bis = reportService.exportPurchasesToExcel(start, end);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=purchases_report.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(bis));
    }

    @GetMapping("/export/customers")
    public ResponseEntity<Resource> exportCustomers() throws Exception {
        ByteArrayInputStream bis = reportService.exportCustomersToExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=customers_report.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(bis));
    }
}
