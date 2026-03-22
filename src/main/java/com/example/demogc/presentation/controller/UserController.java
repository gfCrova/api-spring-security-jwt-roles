package com.example.demogc.presentation.controller;

import com.example.demogc.application.dto.ChangePasswordDTO;
import com.example.demogc.application.dto.UserCreateDTO;
import com.example.demogc.application.dto.UserProfileUpdateDTO;
import com.example.demogc.application.dto.UserResponseDTO;
import com.example.demogc.application.dto.UserRoleUpdateDTO;
import com.example.demogc.application.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Users", description = "Registro, autoservicio y administracion de usuarios")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario con rol USER por defecto.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "409", description = "Username o email duplicado")
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> saveUser(@Valid @RequestBody UserCreateDTO userDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveUser(userDTO));
    }

    @Operation(summary = "Obtener mi perfil", description = "Devuelve el perfil del usuario autenticado.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil encontrado",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/users/me")
    public ResponseEntity<UserResponseDTO> getMyProfile(Authentication authentication) {
        return ResponseEntity.ok(userService.getUserByUsername(authentication.getName()));
    }

    @Operation(summary = "Actualizar mi perfil", description = "Permite al usuario autenticado modificar sus datos editables.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil actualizado",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "409", description = "Email duplicado")
    })
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/users/me")
    public ResponseEntity<UserResponseDTO> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody UserProfileUpdateDTO request
    ) {
        return ResponseEntity.ok(userService.updateOwnProfile(authentication.getName(), request));
    }

    @Operation(summary = "Cambiar mi contraseña", description = "Cambia la contraseña del usuario autenticado validando la contraseña actual.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contraseña actualizada"),
            @ApiResponse(responseCode = "400", description = "Contraseña actual incorrecta o payload invalido"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/users/me/password")
    public ResponseEntity<Void> changeMyPassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordDTO request
    ) {
        userService.changeOwnPassword(authentication.getName(), request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Eliminar mi cuenta", description = "Elimina la cuenta asociada al usuario autenticado.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cuenta eliminada"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "409", description = "No se puede eliminar al ultimo ADMIN")
    })
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/users/me")
    public ResponseEntity<Void> deleteMyAccount(Authentication authentication) {
        userService.deleteOwnAccount(authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar usuarios", description = "Devuelve todos los usuarios del sistema. Solo ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponseDTO.class)))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> findAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Obtener usuario por id", description = "Consulta un usuario por su identificador. Solo ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> findUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "Actualizar roles de un usuario", description = "Permite a un ADMIN promover o degradar roles.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Roles actualizados",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "409", description = "No se puede quitar el rol ADMIN al ultimo administrador")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/users/{id}/roles")
    public ResponseEntity<UserResponseDTO> updateUserRoles(
            @PathVariable Long id,
            @Valid @RequestBody UserRoleUpdateDTO request
    ) {
        return ResponseEntity.ok(userService.updateUserRoles(id, request));
    }

    @Operation(summary = "Eliminar usuario por id", description = "Elimina un usuario del sistema. Solo ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "409", description = "No se puede eliminar al ultimo ADMIN")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
