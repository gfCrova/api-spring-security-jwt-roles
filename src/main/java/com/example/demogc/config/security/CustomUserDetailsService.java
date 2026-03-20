package com.example.demogc.config.security;

import com.example.demogc.model.User;
import com.example.demogc.repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * <h5>Implementación personalizada de UserDetailsService.</h5>
 *
 * <p>Esta clase es utilizada por Spring Security para cargar los datos
 * de un usuario desde la base de datos durante el proceso de autenticación.</p>
 * <ul>Su responsabilidad principal es:
 * <li>1. Buscar un usuario por username.</li>
 * <li>2. Validar su existencia.</li>
 * <li>3. Convertirlo a un objeto UserDetails que Spring Security entiende.</li>
 * </ul>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Es llamado automáticamente por Spring Security cuando alguien intenta autenticarse.
    @Override
    public @NullMarked UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                getAuthority(user)
        );
    }

    // Convierte los ROLES del usuario a un formato que Spring entiende.
    private Set<SimpleGrantedAuthority> getAuthority(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        });
        return authorities;
    }
}
