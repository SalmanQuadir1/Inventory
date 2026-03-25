package com.medicalstore.inventory.controller;

import com.medicalstore.inventory.dto.AnalyticsDto;
import com.medicalstore.inventory.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/management")
@RequiredArgsConstructor
public class ManagementController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        AnalyticsDto analytics = analyticsService.getManagementDashboardData();
        model.addAttribute("analytics", analytics);
        model.addAttribute("activePage", "management");
        return "management/dashboard";
    }
}
