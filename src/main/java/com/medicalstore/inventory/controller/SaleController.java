package com.medicalstore.inventory.controller;

import com.medicalstore.inventory.dto.SaleDto;
import com.medicalstore.inventory.service.ProductService;
import com.medicalstore.inventory.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;
    private final ProductService productService;

    @GetMapping
    public String listSales(
            @RequestParam(value = "page", defaultValue = "1") int pageNo,
            @RequestParam(value = "size", defaultValue = "10") int pageSize,
            @RequestParam(value = "sortField", defaultValue = "saleDate") String sortField,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
            Model model) {
        
        org.springframework.data.domain.Page<SaleDto> page = saleService.getPaginatedSales(pageNo, pageSize, sortField, sortDir);
        
        model.addAttribute("sales", page.getContent());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        
        return "sales/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        SaleDto dto = new SaleDto();
        dto.setPaymentMethod("CASH");
        model.addAttribute("saleDto", dto);
        model.addAttribute("categories", productService.getDistinctCategories());
        return "sales/form";
    }

    @PostMapping("/save")
    public String saveSale(@Valid @ModelAttribute("saleDto") SaleDto saleDto,
            BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            System.err.println("Sale Validation Errors: " + result.getAllErrors());
            model.addAttribute("categories", productService.getDistinctCategories());
            return "sales/form";
        }

        try {
            saleService.createSale(saleDto);
            redirectAttributes.addFlashAttribute("successMessage", "Bill created successfully!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("categories", productService.getDistinctCategories());
            return "sales/form";
        }

        return "redirect:/sales";
    }

    @GetMapping("/{id}")
    public String viewSale(@PathVariable Long id, Model model) {
        model.addAttribute("sale", saleService.getSaleById(id));
        return "sales/view";
    }
}
