package com.example.demogc.application.service;

import com.example.demogc.application.dto.UserCreateDTO;
import com.example.demogc.application.dto.UserResponseDTO;
import com.example.demogc.application.dto.UserRoleUpdateDTO;

import java.util.List;

public interface UserService {

    UserResponseDTO getUserById(Long id);
    UserResponseDTO getUserByUsername(String username);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO saveUser(UserCreateDTO userDTO);
    UserResponseDTO updateUserRoles(Long id, UserRoleUpdateDTO request);
    void deleteUserById(Long id);
}
