package com.medicalstore.inventory.dto;

import com.medicalstore.inventory.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Set;
import java.util.List;

@Data
public class UserDto {
    private Long id;
    
    @NotBlank(message = "Username is required")
    private String username;
    
    private String password;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @Email(message = "Invalid email format")
    private String email;
    
    @NotNull(message = "Role is required")
    private Role role;
    
    private boolean enabled = true;
    
    private Set<Long> warehouseIds;
    private List<String> warehouseNames;
}
