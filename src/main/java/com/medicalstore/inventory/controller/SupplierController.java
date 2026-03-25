package com.medicalstore.inventory.controller;

import com.medicalstore.inventory.dto.SupplierDto;
import com.medicalstore.inventory.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public String listSuppliers(
            @RequestParam(value = "page", defaultValue = "1") int pageNo,
            @RequestParam(value = "size", defaultValue = "10") int pageSize,
            @RequestParam(value = "sortField", defaultValue = "supplierName") String sortField,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            Model model) {
        
        org.springframework.data.domain.Page<SupplierDto> page = supplierService.getPaginatedSuppliers(pageNo, pageSize, sortField, sortDir);
        
        model.addAttribute("suppliers", page.getContent());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        
        return "suppliers/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("supplierDto", new SupplierDto());
        return "suppliers/form";
    }

    @PostMapping("/save")
    public String saveSupplier(@Valid @ModelAttribute("supplierDto") SupplierDto supplierDto, 
                               BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "suppliers/form";
        }
        
        try {
            if (supplierDto.getId() == null) {
                supplierService.createSupplier(supplierDto);
                redirectAttributes.addFlashAttribute("successMessage", "Supplier added successfully!");
            } else {
                supplierService.updateSupplier(supplierDto.getId(), supplierDto);
                redirectAttributes.addFlashAttribute("successMessage", "Supplier updated successfully!");
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "suppliers/form";
        }
        
        return "redirect:/suppliers";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("supplierDto", supplierService.getSupplierById(id));
        return "suppliers/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteSupplier(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            supplierService.deleteSupplier(id);
            redirectAttributes.addFlashAttribute("successMessage", "Supplier deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/suppliers";
    }
}
