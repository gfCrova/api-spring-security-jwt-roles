package com.example.demogc.config.security;

import com.example.demogc.config.security.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/** <h5>Clase de configuración principal de Spring Security.</h5>
 * <ul>Define:
 * <li>- Cómo se autentican los usuarios</li>
 * <li>- Qué endpoints están protegidos</li>
 * <li>- Qué filtros se aplican</li>
 * <li>- Cómo se manejan los errores de seguridad</li>
 * <li>- Uso de JWT (stateless)</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // ================== ATTRIBUTES =============================

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UnauthorizedEntryPoint unauthorizedEntryPoint;

    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;


    // ==================== BEANS ================================

    // Define cómo se autentica un usuario.
    //  - Usa base de datos para autenticar (DaoAuthenticationProvider)
    //  - Carga usuario (UserDetailsService)
    //  - Verifica contraseña (PasswordEncoder)
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(encoder());
        return authProvider;
    }

    // Es el motor de autenticación de Spring. Se usa en login (AuthController)
    @Bean
    public AuthenticationManager  authenticationManagerBean(AuthenticationConfiguration authConfig)  throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Encripta con BCryp las contraseñas y también las valida
    @Bean
    public BCryptPasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

    // Registra tu filtro JWT. Se ejecuta en cada request.
    @Bean
    public JwtAuthenticationFilter authenticationTokenFilterBean() {
        return new JwtAuthenticationFilter();
    }

    // Define toda la configuración de seguridad.
    @Bean
    public SecurityFilterChain  securityFilterChain(HttpSecurity http) {
        http
                // Deshabilitar CORS de Spring. Se usa CORSFilter.
                .cors(AbstractHttpConfigurer::disable)
                // Deshabilitar CSRF, Necesario para APIs REST con JWT.
                .csrf(AbstractHttpConfigurer::disable)
                // Manejar errores de autenticación
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(unauthorizedEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                // Sin sesiones (se usa JWT), Cada request es independiente.
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Configuración de endPoints
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/authentication", "/api/register").permitAll()
                        .anyRequest().authenticated()
                ).authenticationProvider(authenticationProvider());

                // Agregar filtro JWT antes del filtro de login de Spring.
                http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
                return http.build();
    }
}
