package com.medicalstore.inventory.controller;

import com.medicalstore.inventory.dto.CustomerDto;
import com.medicalstore.inventory.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;

import java.math.BigDecimal;

@Controller
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public String listCustomers(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int pageNo,
            @RequestParam(value = "size", defaultValue = "10") int pageSize,
            @RequestParam(value = "sortField", defaultValue = "name") String sortField,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            Model model) {
        
        var page = customerService.getPaginatedCustomers(pageNo, pageSize, sortField, sortDir, keyword);
        
        model.addAttribute("customers", page.getContent());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("keyword", keyword);
        
        return "customers/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("customerDto", new CustomerDto());
        return "customers/form";
    }

    @PostMapping("/save")
    public String saveCustomer(@Valid @ModelAttribute("customerDto") CustomerDto customerDto,
            BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "customers/form";
        }
        try {
            if (customerDto.getId() == null) {
                customerService.createCustomer(customerDto);
                redirectAttributes.addFlashAttribute("successMessage", "Customer created successfully!");
            } else {
                customerService.updateCustomer(customerDto.getId(), customerDto);
                redirectAttributes.addFlashAttribute("successMessage", "Customer updated successfully!");
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "customers/form";
        }
        return "redirect:/customers";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("customerDto", customerService.getCustomerById(id));
        return "customers/form";
    }

    @GetMapping("/{id}")
    public String viewCustomer(@PathVariable Long id, Model model) {
        model.addAttribute("customer", customerService.getCustomerById(id));
        model.addAttribute("transactions", customerService.getCustomerLedger(id));
        return "customers/view";
    }

    @PostMapping("/{id}/payment")
    public String recordPayment(@PathVariable Long id, 
                              @RequestParam("amount") BigDecimal amount,
                              @RequestParam("notes") String notes,
                              RedirectAttributes redirectAttributes) {
        try {
            customerService.recordPayment(id, amount, notes);
            redirectAttributes.addFlashAttribute("successMessage", "Payment recorded successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/customers/" + id;
    }

    @GetMapping("/api/search")
    @ResponseBody
    public java.util.List<CustomerDto> searchApi() {
        return customerService.getAllCustomersForStore();
    }
}
