package com.example.demogc.controller;

import com.example.demogc.dto.UserCreateDTO;
import com.example.demogc.dto.UserResponseDTO;
import com.example.demogc.exception.EmailAlreadyExistsException;
import com.example.demogc.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@CrossOrigin(origins = "*", maxAge = 3600)// Permite peticiones desde cualquier origen (CORS), maxAge: cache del preflight (1 hora)
@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * <h5>Endpoint: Registro</h5>
     * Registra un nuevo usuario en el sistema.
     */
    @PostMapping("/register")
    public UserResponseDTO saveUser(@Valid @RequestBody UserCreateDTO userDTO) throws ResponseStatusException {
        if(userDTO.getEmail() == null || userDTO.getEmail().isEmpty()) {
            throw new EmailAlreadyExistsException("Email ocupado");
        }
        return userService.saveUser(userDTO);
    }

    /**
     * <h5>Endpoint: Obtener usuarios</h5>
     * Devuelve todos los usuarios.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> findAllUsers() {
        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    /**
     * <h5>Endpoint: Obtener usuario por ID</h5>
     * Obtiene un usuario por su ID.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> findUserById(@PathVariable Long id) {
        if(id <= 0) {
            throw new IllegalArgumentException("Invalid ID");
        }
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * <h5>EndPoint: Borrar usuario por ID</h5>
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) throws ResponseStatusException {
        if (id == null) {
            System.out.println("Error");
        }
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
