package com.example.demogc.application.mapper;

import com.example.demogc.application.dto.UserCreateDTO;
import com.example.demogc.application.dto.UserResponseDTO;
import com.example.demogc.domain.model.Role;
import com.example.demogc.domain.model.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserResponseDTO toResponseDTO(User user){
        UserResponseDTO userDTO = new UserResponseDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setPhone(user.getPhone());
        userDTO.setBusinessTitle(user.getBusinessTitle());
        userDTO.setRoles(user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet()));
        return userDTO;
    }

    public User toUser(UserCreateDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setPhone(dto.getPhone());
        user.setBusinessTitle(dto.getBusinessTitle());
        return user;
    }
}
