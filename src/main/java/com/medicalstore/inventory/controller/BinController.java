package com.medicalstore.inventory.controller;

import com.medicalstore.inventory.entity.Bin;
import com.medicalstore.inventory.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/bins")
@RequiredArgsConstructor
public class BinController {

    private final LocationService locationService;

    @GetMapping("/new")
    public String showCreateForm(@RequestParam("zoneId") Long zoneId, Model model) {
        Bin bin = new Bin();
        model.addAttribute("bin", bin);
        model.addAttribute("zoneId", zoneId);
        return "bins/form";
    }

    @PostMapping("/save")
    public String saveBin(@RequestParam("zoneId") Long zoneId, @ModelAttribute("bin") Bin bin, RedirectAttributes redirectAttributes) {
        locationService.createBin(zoneId, bin);
        redirectAttributes.addFlashAttribute("successMessage", "Bin created successfully.");
        return "redirect:/zones/" + zoneId;
    }
}
