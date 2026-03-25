package com.medicalstore.inventory.service;

import com.medicalstore.inventory.dto.UserDto;
import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);
    UserDto updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);
    UserDto getUserById(Long id);
    List<UserDto> getAllUsers();
    org.springframework.data.domain.Page<UserDto> getPaginatedUsers(int pageNo, int pageSize, String sortField, String sortDir);
    void toggleUserStatus(Long id);
}
