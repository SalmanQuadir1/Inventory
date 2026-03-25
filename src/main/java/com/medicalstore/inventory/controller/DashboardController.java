package com.medicalstore.inventory.controller;

import com.medicalstore.inventory.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/")
    public String landing() {
        return "landing";
    }

    @GetMapping("/dashboard")
    @SuppressWarnings("null")
    public String dashboard(Model model) {
        model.addAllAttributes(dashboardService.getDashboardData());
        return "dashboard";
    }
}
