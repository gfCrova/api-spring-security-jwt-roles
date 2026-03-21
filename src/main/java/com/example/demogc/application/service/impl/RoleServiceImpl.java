package com.example.demogc.application.service.impl;

import com.example.demogc.application.service.RoleService;
import com.example.demogc.domain.exception.ResourceNotFoundException;
import com.example.demogc.domain.model.Role;
import com.example.demogc.infrastructure.persistence.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service(value = "roleService")
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + name));
    }
}
