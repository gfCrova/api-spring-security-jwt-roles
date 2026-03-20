package com.example.demogc.config.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * <h5>Filtro de autenticación basado en JWT (JSON Web Token).</h5>
 * <p>Esta clase intercepta cada request HTTP y se encarga de:</p>
 * <ul>
 *     <li>1. Obtener el token JWT desde el header de la request.</li>
 *     <li>2. Validarlo.</li>
 *     <li>3. Extraer el usuario.</li>
 *     <li>4. Cargar sus detalles.</li>
 *     <li>5. Autenticarlo en el contexto de Spring Security.</li>
 * </ul>
 * <p>--> Extiende 'OncePerRequestFilter', lo que garantiza que el filtro se ejecute una única vez por cada request.</p>
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.header.string}")
    private String HEADER_STRING;

    @Value("${jwt.token.prefix}")
    private String TOKEN_PREFIX;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenProvider jwtTokenUtil;

    /**
     * <h5>Se ejecuta en cada request HTTP.</h5>
     * Validad estructura del Token
     * Extrae el Token
     *
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader(HEADER_STRING);
        String username = null;
        String authToken = null;

        // - Que exista el header && Que empieze con ("Bearer").
        //-> Elimina el "Bearer" y deja solo el JWT.
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            authToken = header.replace(TOKEN_PREFIX, "");
            try {
                username = jwtTokenUtil.getUsernameFromToken(authToken);
            } catch (IllegalArgumentException e) {
                logger.error("An error occurred while fetching Username from Token", e);
            } catch (ExpiredJwtException e) {
                logger.warn("The token has expired", e);
            } catch (SignatureException e) {
                logger.error("Authentication Failed. Username or Password not valid.");
            }
        } else {
            logger.warn("Couldn't find bearer string, header will be ignored");
        }
        // Verificar autenticación previa
        // Evita re-autenticar si ya existe autenticación en el contexto.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Verifica que el token no esté expirado, que coincida con el usuario.
            if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                // Representa al usuario autenticado dentro de Spring Security.
                UsernamePasswordAuthenticationToken authentication = jwtTokenUtil.getAuthenticationToken(
                        authToken,
                        SecurityContextHolder
                                .getContext()
                                .getAuthentication(),
                        userDetails
                );
                // Añade info como: IP, sesión
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                logger.info("authenticated user " + username + ", setting security context");
                // A partir de acá, el usuario queda autenticado en toda la app.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
