package com.example.demogc.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.bootstrap.admin")
public record AdminBootstrapProperties(
        boolean enabled,
        String username,
        String email,
        String password,
        String name,
        Long phone,
        String businessTitle
) {
}
