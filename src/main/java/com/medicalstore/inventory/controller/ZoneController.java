package com.medicalstore.inventory.controller;

import com.medicalstore.inventory.entity.Zone;
import com.medicalstore.inventory.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/zones")
@RequiredArgsConstructor
public class ZoneController {

    private final LocationService locationService;

    @GetMapping("/new")
    public String showCreateForm(@RequestParam("warehouseId") Long warehouseId, Model model) {
        Zone zone = new Zone();
        model.addAttribute("zone", zone);
        model.addAttribute("warehouseId", warehouseId);
        return "zones/form";
    }

    @PostMapping("/save")
    public String saveZone(@RequestParam("warehouseId") Long warehouseId, @ModelAttribute("zone") Zone zone, RedirectAttributes redirectAttributes) {
        locationService.createZone(warehouseId, zone);
        redirectAttributes.addFlashAttribute("successMessage", "Zone created successfully.");
        return "redirect:/warehouses/" + warehouseId;
    }

    @GetMapping("/{id}")
    public String viewZone(@PathVariable Long id, Model model) {
        Zone zone = locationService.getZoneById(id);
        model.addAttribute("zone", zone);
        model.addAttribute("bins", locationService.getBinsByZone(id));
        return "zones/view";
    }
}
