package com.example.demogc.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Credenciales de autenticacion")
public class UserLoginDTO {

    @NotBlank
    @Schema(example = "superadmin")
    private String username;

    @NotBlank
    @Schema(example = "ChangeMeNow123!")
    private String password;
}
