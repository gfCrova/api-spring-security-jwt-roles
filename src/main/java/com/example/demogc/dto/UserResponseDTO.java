package com.example.demogc.dto;

import com.example.demogc.model.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UserResponseDTO {
    private String username;
    private String email;
    private String name;
    private Long phone;
    private String businessTitle;
    private Set<Role> roles;
}
