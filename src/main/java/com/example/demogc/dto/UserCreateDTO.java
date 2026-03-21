package com.example.demogc.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreateDTO {
    @NotBlank
    @Size(min = 4, max = 50)
    @Pattern(regexp = "^[A-Za-z0-9._-]+$", message = "Username solo puede contener letras, numeros, punto, guion y guion bajo")
    private String username;

    @NotBlank
    @Email
    @Size(max = 120)
    private String email;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    @NotBlank
    @Size(max = 120)
    private String name;

    @NotNull
    @Positive
    private Long phone;

    @NotBlank
    @Size(max = 120)
    private String businessTitle;
}
