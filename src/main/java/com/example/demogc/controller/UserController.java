package com.example.demogc.controller;

import com.example.demogc.dto.UserCreateDTO;
import com.example.demogc.dto.UserResponseDTO;
import com.example.demogc.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> saveUser(@Valid @RequestBody UserCreateDTO userDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveUser(userDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> findAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> findUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
