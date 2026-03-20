package com.example.demogc.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserCreateDTO {
    @NotBlank
    private String username;
    @Email
    private String email;
    @NotBlank
    private String password;
    private String name;
    private Long phone;
    private String businessTitle;
}
