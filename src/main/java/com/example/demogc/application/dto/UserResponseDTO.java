package com.example.demogc.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;

@Data
@Schema(description = "Respuesta publica de usuario")
public class UserResponseDTO {
    @Schema(example = "1")
    private Long id;
    @Schema(example = "juanma")
    private String username;
    @Schema(example = "juan@example.com")
    private String email;
    @Schema(example = "Juan Manuel")
    private String name;
    @Schema(example = "123456789")
    private Long phone;
    @Schema(example = "Backend Developer")
    private String businessTitle;
    private Set<String> roles;
}
