package com.example.demogc.service.impl;

import com.example.demogc.model.Role;
import com.example.demogc.exception.ResourceNotFoundException;
import com.example.demogc.repository.RoleRepository;
import com.example.demogc.service.RoleService;
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
