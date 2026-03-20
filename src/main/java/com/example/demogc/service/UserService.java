package com.example.demogc.service;

import com.example.demogc.dto.UserCreateDTO;
import com.example.demogc.dto.UserResponseDTO;

import java.util.List;

public interface UserService {

    UserResponseDTO getUserById(Long id);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO saveUser(UserCreateDTO userDTO);
    void deleteUserById(Long id);
}
