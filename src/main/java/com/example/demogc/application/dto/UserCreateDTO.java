package com.example.demogc.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Datos necesarios para registrar un usuario")
public class UserCreateDTO {
    @NotBlank
    @Size(min = 4, max = 50)
    @Pattern(regexp = "^[A-Za-z0-9._-]+$", message = "Username solo puede contener letras, numeros, punto, guion y guion bajo")
    @Schema(example = "juan")
    private String username;

    @NotBlank
    @Email
    @Size(max = 120)
    @Schema(example = "juan@example.com")
    private String email;

    @NotBlank
    @Size(min = 8, max = 100)
    @Schema(example = "Secret1234")
    private String password;

    @NotBlank
    @Size(max = 120)
    @Schema(example = "Juan Manuel")
    private String name;

    @NotNull
    @Positive
    @Schema(example = "123456789")
    private Long phone;

    @NotBlank
    @Size(max = 120)
    @Schema(example = "Backend Developer")
    private String businessTitle;
}
