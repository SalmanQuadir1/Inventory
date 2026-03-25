package com.medicalstore.inventory.controller;

import com.medicalstore.inventory.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
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
}
