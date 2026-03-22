package com.example.demogc.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "JWT emitido tras autenticacion exitosa")
public class AuthToken {

    @Schema(example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;
}
