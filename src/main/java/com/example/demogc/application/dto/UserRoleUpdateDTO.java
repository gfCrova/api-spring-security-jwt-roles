package com.example.demogc.application.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class UserRoleUpdateDTO {

    @NotEmpty
    private Set<String> roles;
}
