package com.example.demogc.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Datos editables del perfil del usuario autenticado")
public class UserProfileUpdateDTO {

    @NotBlank
    @Email
    @Size(max = 120)
    @Schema(example = "nuevo@email.com")
    private String email;

    @NotBlank
    @Size(max = 120)
    @Schema(example = "Nombre Actualizado")
    private String name;

    @NotNull
    @Positive
    @Schema(example = "123456789")
    private Long phone;

    @NotBlank
    @Size(max = 120)
    @Schema(example = "Backend Engineer")
    private String businessTitle;
}
