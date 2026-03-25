package com.medicalstore.inventory.controller;

import com.medicalstore.inventory.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping("/ledger/{productId}")
    public String viewLedger(@PathVariable Long productId, Model model) {
        model.addAttribute("transactions", stockService.getTransactionsByProduct(productId));
        return "stock/ledger";
    }

    @PostMapping("/transfer")
    public String transferStock(@RequestParam("productId") Long productId, 
                                @RequestParam("fromBinId") Long fromBinId, 
                                @RequestParam("toBinId") Long toBinId, 
                                @RequestParam("quantity") Integer quantity,
                                @RequestParam(value = "reference", required = false) String reference,
                                RedirectAttributes redirectAttributes) {
        try {
            stockService.transferStock(productId, fromBinId, toBinId, quantity, reference != null ? reference : "TRANSFER-SYS");
            redirectAttributes.addFlashAttribute("successMessage", "Stock transferred successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/stock/ledger/" + productId;
    }
}
