package com.example.demogc.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Solicitud para cambiar la contraseña propia")
public class ChangePasswordDTO {

    @NotBlank
    @Schema(example = "Secret123")
    private String currentPassword;

    @NotBlank
    @Size(min = 8, max = 100)
    @Schema(example = "Secret456")
    private String newPassword;
}
