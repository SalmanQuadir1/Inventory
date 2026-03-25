package com.medicalstore.inventory.controller;

import com.medicalstore.inventory.dto.PurchaseDto;
import com.medicalstore.inventory.service.PurchaseService;
import com.medicalstore.inventory.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final SupplierService supplierService;
    private final com.medicalstore.inventory.repository.BinRepository binRepository;

    @GetMapping
    public String listPurchases(
            @RequestParam(value = "page", defaultValue = "1") int pageNo,
            @RequestParam(value = "size", defaultValue = "10") int pageSize,
            @RequestParam(value = "sortField", defaultValue = "purchaseDate") String sortField,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
            Model model) {
        
        org.springframework.data.domain.Page<PurchaseDto> page = purchaseService.getPaginatedPurchases(pageNo, pageSize, sortField, sortDir);
        
        model.addAttribute("purchases", page.getContent());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        
        return "purchases/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("purchaseDto", new PurchaseDto());
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        model.addAttribute("bins", binRepository.findAll());
        return "purchases/form";
    }

    @PostMapping("/save")
    public String savePurchase(@Valid @ModelAttribute("purchaseDto") PurchaseDto purchaseDto,
            BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            System.err.println("Purchase Validation Errors: " + result.getAllErrors());
            model.addAttribute("suppliers", supplierService.getAllSuppliers());
            return "purchases/form";
        }

        try {
            purchaseService.createPurchase(purchaseDto);
            redirectAttributes.addFlashAttribute("successMessage", "Purchase order created successfully!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("suppliers", supplierService.getAllSuppliers());
            return "purchases/form";
        }

        return "redirect:/purchases";
    }

    @GetMapping("/{id}")
    public String viewPurchase(@PathVariable Long id, Model model) {
        model.addAttribute("purchase", purchaseService.getPurchaseById(id));
        return "purchases/view";
    }
}
