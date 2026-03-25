package com.medicalstore.inventory.controller;

import com.medicalstore.inventory.entity.Warehouse;
import com.medicalstore.inventory.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final LocationService locationService;

    @GetMapping
    public String listWarehouses(Model model) {
        model.addAttribute("warehouses", locationService.getAllWarehouses());
        return "warehouses/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("warehouse", new Warehouse());
        return "warehouses/form";
    }

    @PostMapping("/save")
    public String saveWarehouse(@ModelAttribute("warehouse") Warehouse warehouse, RedirectAttributes redirectAttributes) {
        try {
            if(warehouse.getId() != null) {
                locationService.updateWarehouse(warehouse.getId(), warehouse);
                redirectAttributes.addFlashAttribute("successMessage", "Warehouse updated successfully.");
            } else {
                locationService.createWarehouse(warehouse);
                redirectAttributes.addFlashAttribute("successMessage", "Warehouse created successfully.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving warehouse: " + e.getMessage());
            return "redirect:/warehouses/new";
        }
        return "redirect:/warehouses";
    }

    @GetMapping("/{id}")
    public String viewWarehouse(@PathVariable Long id, Model model) {
        model.addAttribute("warehouse", locationService.getWarehouseById(id));
        model.addAttribute("zones", locationService.getZonesByWarehouse(id));
        return "warehouses/view";
    }
}
