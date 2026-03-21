package com.example.demogc.application.service.impl;

import com.example.demogc.application.dto.UserCreateDTO;
import com.example.demogc.application.dto.UserResponseDTO;
import com.example.demogc.application.dto.UserRoleUpdateDTO;
import com.example.demogc.application.mapper.UserMapper;
import com.example.demogc.application.service.RoleService;
import com.example.demogc.application.service.UserService;
import com.example.demogc.domain.exception.EmailAlreadyExistsException;
import com.example.demogc.domain.exception.ResourceNotFoundException;
import com.example.demogc.domain.exception.UsernameAlreadyExistsException;
import com.example.demogc.domain.model.Role;
import com.example.demogc.domain.model.User;
import com.example.demogc.infrastructure.persistence.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {

    private final RoleService roleService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserMapper userMapper;

    public UserServiceImpl(
            RoleService roleService,
            UserRepository userRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            UserMapper userMapper
    ) {
        this.roleService = roleService;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        return userMapper.toResponseDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username " + username));
        return userMapper.toResponseDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponseDTO)
                .toList();
    }

    @Override
    public UserResponseDTO saveUser(UserCreateDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User newUser = userMapper.toUser(userDTO);
        newUser.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));

        Role defaultRole = roleService.findByName("USER");
        newUser.setRoles(Set.of(defaultRole));

        User user = userRepository.save(newUser);
        return userMapper.toResponseDTO(user);
    }

    @Override
    public UserResponseDTO updateUserRoles(Long id, UserRoleUpdateDTO request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        Set<Role> roles = request.getRoles()
                .stream()
                .map(roleName -> roleName.trim().toUpperCase(Locale.ROOT))
                .map(roleService::findByName)
                .collect(Collectors.toSet());

        user.setRoles(roles);
        return userMapper.toResponseDTO(userRepository.save(user));
    }

    @Override
    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id " + id);
        }
        userRepository.deleteById(id);
    }
}
