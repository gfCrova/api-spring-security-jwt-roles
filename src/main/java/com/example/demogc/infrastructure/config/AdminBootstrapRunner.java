package com.example.demogc.infrastructure.config;

import com.example.demogc.application.service.RoleService;
import com.example.demogc.domain.model.Role;
import com.example.demogc.domain.model.User;
import com.example.demogc.infrastructure.persistence.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
public class AdminBootstrapRunner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminBootstrapRunner.class);

    private final AdminBootstrapProperties properties;
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AdminBootstrapRunner(
            AdminBootstrapProperties properties,
            UserRepository userRepository,
            RoleService roleService,
            BCryptPasswordEncoder passwordEncoder
    ) {
        this.properties = properties;
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!properties.enabled()) {
            return;
        }

        if (userRepository.existsByRoles_Name("ADMIN")) {
            LOGGER.info("Admin bootstrap skipped because an ADMIN user already exists");
            return;
        }

        validateProperties();

        Role adminRole = roleService.findByName("ADMIN");
        Role userRole = roleService.findByName("USER");

        User admin = new User();
        admin.setUsername(properties.username());
        admin.setEmail(properties.email());
        admin.setPassword(passwordEncoder.encode(properties.password()));
        admin.setName(properties.name());
        admin.setPhone(properties.phone());
        admin.setBusinessTitle(properties.businessTitle());
        admin.setRoles(Set.of(adminRole, userRole));

        userRepository.save(admin);
        LOGGER.warn("Bootstrap ADMIN user created successfully for username '{}'. Disable app.bootstrap.admin.enabled after first use.", properties.username());
    }

    private void validateProperties() {
        if (isBlank(properties.username())
                || isBlank(properties.email())
                || isBlank(properties.password())
                || isBlank(properties.name())
                || properties.phone() == null
                || isBlank(properties.businessTitle())) {
            throw new IllegalStateException("Admin bootstrap is enabled but required properties are missing");
        }

        if (userRepository.existsByUsername(properties.username())) {
            throw new IllegalStateException("Bootstrap admin username already exists");
        }
        if (userRepository.existsByEmail(properties.email())) {
            throw new IllegalStateException("Bootstrap admin email already exists");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
