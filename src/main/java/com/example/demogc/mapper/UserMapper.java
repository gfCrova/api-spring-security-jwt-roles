package com.example.demogc.mapper;

import com.example.demogc.dto.UserCreateDTO;
import com.example.demogc.dto.UserResponseDTO;
import com.example.demogc.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDTO toResponseDTO(User user){
        UserResponseDTO userDTO = new UserResponseDTO();
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setPhone(user.getPhone());
        userDTO.setBusinessTitle(user.getBusinessTitle());
        userDTO.setRoles(user.getRoles());
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
