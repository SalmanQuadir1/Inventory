package com.medicalstore.inventory.service.impl;

import com.medicalstore.inventory.dto.UserDto;
import com.medicalstore.inventory.entity.User;
import com.medicalstore.inventory.entity.Warehouse;
import com.medicalstore.inventory.exception.ResourceNotFoundException;
import com.medicalstore.inventory.repository.UserRepository;
import com.medicalstore.inventory.repository.WarehouseRepository;
import com.medicalstore.inventory.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @SuppressWarnings("null")
    public UserDto createUser(UserDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = mapToEntity(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        
        if (userDto.getWarehouseIds() != null && !userDto.getWarehouseIds().isEmpty()) {
            java.util.Set<Warehouse> warehouses = new java.util.HashSet<>(warehouseRepository.findAllById(userDto.getWarehouseIds()));
            user.setWarehouses(warehouses);
        }
        
        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    @Override
    @SuppressWarnings("null")
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setRole(userDto.getRole());
        
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        
        if (userDto.getWarehouseIds() != null && !userDto.getWarehouseIds().isEmpty()) {
            java.util.Set<Warehouse> warehouses = new java.util.HashSet<>(warehouseRepository.findAllById(userDto.getWarehouseIds()));
            user.setWarehouses(warehouses);
        } else {
            user.getWarehouses().clear();
        }
        
        User updatedUser = userRepository.save(user);
        return mapToDto(updatedUser);
    }

    @Override
    @SuppressWarnings("null")
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }

    @Override
    @SuppressWarnings("null")
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("null")
    public org.springframework.data.domain.Page<UserDto> getPaginatedUsers(int pageNo, int pageSize, String sortField, String sortDir) {
        org.springframework.data.domain.Sort sort = sortDir.equalsIgnoreCase("asc") ? 
            org.springframework.data.domain.Sort.by(sortField).ascending() : 
            org.springframework.data.domain.Sort.by(sortField).descending();
            
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(pageNo - 1, pageSize, sort);
        
        org.springframework.data.domain.Page<User> page = userRepository.findAll(pageable);
        
        List<UserDto> dtoList = page.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
                
        return new org.springframework.data.domain.PageImpl<>(dtoList, pageable, page.getTotalElements());
    }

    @Override
    @SuppressWarnings("null")
    public void toggleUserStatus(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    private UserDto mapToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setEnabled(user.isEnabled());
        
        if (user.getWarehouses() != null && !user.getWarehouses().isEmpty()) {
            dto.setWarehouseIds(user.getWarehouses().stream().map(Warehouse::getId).collect(java.util.stream.Collectors.toSet()));
            dto.setWarehouseNames(user.getWarehouses().stream().map(Warehouse::getName).collect(java.util.stream.Collectors.toList()));
        }
        
        return dto;
    }

    private User mapToEntity(UserDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user.setEnabled(dto.isEnabled());
        return user;
    }
}
