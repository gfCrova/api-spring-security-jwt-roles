package com.example.demogc.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
@Schema(description = "Cambio de roles para un usuario")
public class UserRoleUpdateDTO {

    @NotEmpty
    @Schema(example = "[\"ADMIN\", \"USER\"]")
    private Set<String> roles;
}
