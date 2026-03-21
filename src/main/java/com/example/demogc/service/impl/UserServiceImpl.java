package com.example.demogc.service.impl;

import com.example.demogc.dto.UserCreateDTO;
import com.example.demogc.dto.UserResponseDTO;
import com.example.demogc.exception.EmailAlreadyExistsException;
import com.example.demogc.exception.ResourceNotFoundException;
import com.example.demogc.exception.UsernameAlreadyExistsException;
import com.example.demogc.mapper.UserMapper;
import com.example.demogc.model.Role;
import com.example.demogc.model.User;
import com.example.demogc.repository.UserRepository;
import com.example.demogc.service.RoleService;
import com.example.demogc.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

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
    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id " + id);
        }
        userRepository.deleteById(id);
    }
}
