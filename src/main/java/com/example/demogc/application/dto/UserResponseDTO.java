package com.example.demogc.application.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String name;
    private Long phone;
    private String businessTitle;
    private Set<String> roles;
}
