package com.medicalstore.inventory.controller;

import com.medicalstore.inventory.dto.UserDto;
import com.medicalstore.inventory.entity.Role;
import com.medicalstore.inventory.service.UserService;
import com.medicalstore.inventory.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final LocationService locationService;

    @GetMapping
    public String listUsers(
            @RequestParam(value = "page", defaultValue = "1") int pageNo,
            @RequestParam(value = "size", defaultValue = "10") int pageSize,
            @RequestParam(value = "sortField", defaultValue = "name") String sortField,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            Model model) {
        
        org.springframework.data.domain.Page<UserDto> page = userService.getPaginatedUsers(pageNo, pageSize, sortField, sortDir);
        
        model.addAttribute("users", page.getContent());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        
        return "users/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        model.addAttribute("roles", Role.values());
        model.addAttribute("warehouses", locationService.getAllWarehouses());
        return "users/form";
    }

    @PostMapping("/save")
    public String saveUser(@Valid @ModelAttribute("userDto") UserDto userDto, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        
        // Defensive manual check since password is conditionally required
        if (userDto.getId() == null && (userDto.getPassword() == null || userDto.getPassword().trim().isEmpty())) {
            result.rejectValue("password", "error.userDto", "Access Password is strictly required for provisioning new operators.");
        }
        
        if (result.hasErrors()) {
            model.addAttribute("roles", Role.values());
            model.addAttribute("warehouses", locationService.getAllWarehouses());
            return "users/form";
        }
        
        try {
            if (userDto.getId() == null) {
                userService.createUser(userDto);
                redirectAttributes.addFlashAttribute("successMessage", "User created successfully!");
            } else {
                userService.updateUser(userDto.getId(), userDto);
                redirectAttributes.addFlashAttribute("successMessage", "User updated successfully!");
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("roles", Role.values());
            model.addAttribute("warehouses", locationService.getAllWarehouses());
            return "users/form";
        }
        
        return "redirect:/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        UserDto userDto = userService.getUserById(id);
        model.addAttribute("userDto", userDto);
        model.addAttribute("roles", Role.values());
        model.addAttribute("warehouses", locationService.getAllWarehouses());
        // For editing, we don't necessarily update password unless provided
        userDto.setPassword(""); 
        return "users/form";
    }

    @GetMapping("/toggle/{id}")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.toggleUserStatus(id);
        redirectAttributes.addFlashAttribute("successMessage", "User status updated!");
        return "redirect:/users";
    }
}
