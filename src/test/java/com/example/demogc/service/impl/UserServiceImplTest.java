package com.example.demogc.service.impl;

import com.example.demogc.dto.UserCreateDTO;
import com.example.demogc.exception.UsernameAlreadyExistsException;
import com.example.demogc.mapper.UserMapper;
import com.example.demogc.model.Role;
import com.example.demogc.model.User;
import com.example.demogc.repository.UserRepository;
import com.example.demogc.service.RoleService;
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
}
