package com.example.demogc.service.impl;

import com.example.demogc.model.Role;
import com.example.demogc.repository.RoleRepository;
import com.example.demogc.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "roleService")
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Role findByName(String name) {
        return roleRepository.findRoleByName(name);
    }
}
