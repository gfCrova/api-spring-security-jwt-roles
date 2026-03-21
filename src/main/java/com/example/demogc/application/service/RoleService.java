package com.example.demogc.application.service;

import com.example.demogc.domain.model.Role;

public interface RoleService {
    Role findByName(String name);
}
