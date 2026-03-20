package com.example.demogc.service.impl;

import com.example.demogc.dto.UserCreateDTO;
import com.example.demogc.dto.UserResponseDTO;
import com.example.demogc.exception.EmailAlreadyExistsException;
import com.example.demogc.mapper.UserMapper;
import com.example.demogc.model.Role;
import com.example.demogc.model.User;
import com.example.demogc.repository.UserRepository;
import com.example.demogc.service.RoleService;
import com.example.demogc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service(value = "userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private RoleService roleService;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,  UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("User Not Found!"));
        return userMapper.toResponseDTO(user);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponseDTO)
                .toList();
    }

    @Override
    public UserResponseDTO saveUser(UserCreateDTO userDTO) {

        // 1. Validación
        if(userRepository.existsByEmail(userDTO.getEmail()))
            throw new EmailAlreadyExistsException("Email ocupado");

        User nUser = userMapper.toUser(userDTO);

        nUser.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));

        Role role = roleService.findByName("USER");
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(role);

        if(nUser.getEmail().split("@")[1].equals("admin.edu")){
            role = roleService.findByName("ADMIN");
            roleSet.add(role);
        }
        nUser.setRoles(roleSet);

        User user = userRepository.save(nUser);
        return userMapper.toResponseDTO(user);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

}