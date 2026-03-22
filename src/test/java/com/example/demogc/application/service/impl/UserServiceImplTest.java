package com.example.demogc.application.service.impl;

import com.example.demogc.application.dto.ChangePasswordDTO;
import com.example.demogc.application.dto.UserCreateDTO;
import com.example.demogc.application.dto.UserProfileUpdateDTO;
import com.example.demogc.application.dto.UserResponseDTO;
import com.example.demogc.application.dto.UserRoleUpdateDTO;
import com.example.demogc.application.mapper.UserMapper;
import com.example.demogc.application.service.RoleService;
import com.example.demogc.domain.exception.UsernameAlreadyExistsException;
import com.example.demogc.domain.model.Role;
import com.example.demogc.domain.model.User;
import com.example.demogc.infrastructure.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private RoleService roleService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    private BCryptPasswordEncoder passwordEncoder;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserServiceImpl(roleService, userRepository, passwordEncoder, userMapper);
    }

    @Test
    void saveUserAssignsDefaultUserRoleAndHashesPassword() {
        UserCreateDTO dto = new UserCreateDTO();
        dto.setUsername("gian");
        dto.setEmail("gian@example.com");
        dto.setPassword("secret123");
        dto.setName("Gian");
        dto.setPhone(123456789L);
        dto.setBusinessTitle("Developer");

        User mappedUser = new User();
        mappedUser.setUsername(dto.getUsername());
        mappedUser.setEmail(dto.getEmail());
        mappedUser.setPassword(dto.getPassword());
        mappedUser.setName(dto.getName());
        mappedUser.setPhone(dto.getPhone());
        mappedUser.setBusinessTitle(dto.getBusinessTitle());

        Role userRole = new Role(1L, "USER", "User role");

        when(userRepository.existsByUsername(dto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(userMapper.toUser(dto)).thenReturn(mappedUser);
        when(roleService.findByName("USER")).thenReturn(userRole);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.saveUser(dto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getRoles()).extracting(Role::getName).containsExactly("USER");
        assertThat(savedUser.getPassword()).isNotEqualTo("secret123");
        assertThat(passwordEncoder.matches("secret123", savedUser.getPassword())).isTrue();
    }

    @Test
    void saveUserRejectsDuplicatedUsername() {
        UserCreateDTO dto = new UserCreateDTO();
        dto.setUsername("existing-user");
        dto.setEmail("existing@example.com");

        when(userRepository.existsByUsername(dto.getUsername())).thenReturn(true);

        assertThatThrownBy(() -> userService.saveUser(dto))
                .isInstanceOf(UsernameAlreadyExistsException.class);
    }

    @Test
    void getUserByUsernameReturnsMappedUser() {
        User user = new User();
        user.setId(4L);
        user.setUsername("gian");

        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(4L);
        responseDTO.setUsername("gian");

        when(userRepository.findByUsername("gian")).thenReturn(java.util.Optional.of(user));
        when(userMapper.toResponseDTO(user)).thenReturn(responseDTO);

        UserResponseDTO result = userService.getUserByUsername("gian");

        assertThat(result.getUsername()).isEqualTo("gian");
    }

    @Test
    void updateOwnProfileUpdatesEditableFields() {
        User user = new User();
        user.setId(10L);
        user.setUsername("gian");
        user.setEmail("old@example.com");
        user.setName("Old Name");
        user.setPhone(123L);
        user.setBusinessTitle("Old Title");

        UserProfileUpdateDTO request = new UserProfileUpdateDTO();
        request.setEmail("new@example.com");
        request.setName("New Name");
        request.setPhone(456L);
        request.setBusinessTitle("New Title");

        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setEmail("new@example.com");
        responseDTO.setName("New Name");

        when(userRepository.findByUsername("gian")).thenReturn(java.util.Optional.of(user));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toResponseDTO(any(User.class))).thenReturn(responseDTO);

        UserResponseDTO result = userService.updateOwnProfile("gian", request);

        assertThat(result.getEmail()).isEqualTo("new@example.com");
        assertThat(user.getName()).isEqualTo("New Name");
        assertThat(user.getPhone()).isEqualTo(456L);
        assertThat(user.getBusinessTitle()).isEqualTo("New Title");
    }

    @Test
    void changeOwnPasswordReplacesStoredHashWhenCurrentPasswordMatches() {
        User user = new User();
        user.setUsername("gian");
        user.setPassword(passwordEncoder.encode("oldPassword123"));

        ChangePasswordDTO request = new ChangePasswordDTO();
        request.setCurrentPassword("oldPassword123");
        request.setNewPassword("newPassword123");

        when(userRepository.findByUsername("gian")).thenReturn(java.util.Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.changeOwnPassword("gian", request);

        assertThat(passwordEncoder.matches("newPassword123", user.getPassword())).isTrue();
    }

    @Test
    void deleteOwnAccountDeletesAuthenticatedUserRecord() {
        User user = new User();
        user.setUsername("gian");
        user.setRoles(Set.of(new Role(1L, "USER", "User role")));

        when(userRepository.findByUsername("gian")).thenReturn(java.util.Optional.of(user));

        userService.deleteOwnAccount("gian");

        verify(userRepository).delete(user);
    }

    @Test
    void deleteOwnAccountRejectsDeletingLastAdmin() {
        User user = new User();
        user.setUsername("admin");
        user.setRoles(Set.of(new Role(1L, "ADMIN", "Admin role")));

        when(userRepository.findByUsername("admin")).thenReturn(java.util.Optional.of(user));
        when(userRepository.countByRoles_Name("ADMIN")).thenReturn(1L);

        assertThatThrownBy(() -> userService.deleteOwnAccount("admin"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("The last ADMIN user cannot be deleted");
    }

    @Test
    void updateUserRolesRejectsRemovingAdminRoleFromLastAdmin() {
        User user = new User();
        user.setId(7L);
        user.setUsername("admin");
        user.setRoles(Set.of(new Role(1L, "ADMIN", "Admin role")));

        UserRoleUpdateDTO request = new UserRoleUpdateDTO();
        request.setRoles(Set.of("USER"));

        when(userRepository.findById(7L)).thenReturn(java.util.Optional.of(user));
        when(roleService.findByName("USER")).thenReturn(new Role(2L, "USER", "User role"));
        when(userRepository.countByRoles_Name("ADMIN")).thenReturn(1L);

        assertThatThrownBy(() -> userService.updateUserRoles(7L, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("The last ADMIN user must keep the ADMIN role");
    }

    @Test
    void updateUserRolesReplacesRolesFromAdminRequest() {
        User user = new User();
        user.setId(7L);
        user.setUsername("candidate");

        Role adminRole = new Role(1L, "ADMIN", "Admin role");
        Role managerRole = new Role(2L, "MANAGER", "Manager role");

        UserRoleUpdateDTO request = new UserRoleUpdateDTO();
        request.setRoles(Set.of("admin", "manager"));

        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(7L);
        responseDTO.setRoles(Set.of("ADMIN", "MANAGER"));

        when(userRepository.findById(7L)).thenReturn(java.util.Optional.of(user));
        when(roleService.findByName("ADMIN")).thenReturn(adminRole);
        when(roleService.findByName("MANAGER")).thenReturn(managerRole);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toResponseDTO(any(User.class))).thenReturn(responseDTO);

        UserResponseDTO result = userService.updateUserRoles(7L, request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getRoles()).extracting(Role::getName)
                .containsExactlyInAnyOrder("ADMIN", "MANAGER");
        assertThat(result.getRoles()).containsExactlyInAnyOrder("ADMIN", "MANAGER");
    }
}
