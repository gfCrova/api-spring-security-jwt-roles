package com.example.demogc.config.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <h5>Descripción general</h5>
 * <p>La clase TokenProvider se encarga de: </p>
 * <ul>
 *     <li>Generar tokens JWT</li>
 *     <li>Extraer información (claims) del token</li>
 *     <li>Validar tokens</li>
 *     <li>Construir objetos de autenticación de Spring Security</li>
 * </ul>
 * <p>Se utiliza dentro del flujo de autenticación basado en JWT.</p>
 */
@Component
public class TokenProvider implements Serializable {

    // ========================= ATTRIBUTES =================================

    // Tiempo de validez del token (en segundos).
    @Value("${jwt.token.validity}")
    public long TOKEN_VALIDITY;

    // Clave secreta utilizada para firmar el token.
    @Value("${jwt.signing.key}")
    public String SIGNING_KEY;

    // Nombre del claim donde se guardan los roles/permisos del usuario.
    @Value("${jwt.authorities.key}")
    public String AUTHORITIES_KEY;


    // ========================= METHODS ===================================

    /**
     * <p>Obtiene el username (subject) del token.</p>
     * <ul>
     *      <li>1. Llama a getClaimFromToken</li>
     *      <li>2. Le pasa una función (Claims::getSubject)</li>
     *      <li>3. Esa función indica que quiere extraer el subject</li>
     * </ul>
     * <p>En JWT: subject = usuario (username)</p>
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * <p>Obtiene la fecha de expiración del token.</p>
     * <ul>
     *      <li>1. Llama a getClaimFromTokenn</li>
     *      <li>2. Usa Claims::getExpiration</li>
     *      <li>3. Devuelve un Date</li>
     * </ul>
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * <p>Es un metodo genérico para obtener cualquier dato (claim) del token.</p>
     * <ul>
     *      <li>1. Obtiene todos los claims con: getAllClaimsFromToken(token)</li>
     *      <li>2. Aplica una función (claimsResolver) sobre esos claims</li>
     *      <li>3. Devuelve el resultado</li>
     * </ul>
     */
    public <T> T getClaimFromToken(String token, @NonNull Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * <p>Obtiene todos los datos (claims) del token.</p>
     * <ul>
     *      <li>1. Crea un parser JWT</li>
     *      <li>2. Le pasa la clave secreta (SIGNING_KEY)</li>
     *      <li>3. Parsea el token</li>
     *      <li>4. Obtiene el body (claims)</li>
     * </ul>
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SIGNING_KEY.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * <p>Verifica si el token está expirado.</p>
     * <ul>
     *      <li>1. Obtiene la fecha de expiración</li>
     *      <li>2. Compara con la fecha actual</li>
     *      <li>3. Retorna: true → expirado, false → válido</li>
     * </ul>
     */
    private @NonNull Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * <P>Genera un JWT a partir de un usuario autenticado.</p>
     * <ul>
     *      <li>1. Obtener roles del usuario</li>
     *      <li>2. Construir el token</li>
     *      <li>3. Compactar token</li>
     * </ul>
     */
    public String generateToken(@NonNull Authentication authentication) {
        // 1. Convierte roles a string: "ROLE_USER,ROLE_ADMIN"
        String authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // 2. Se configuran los datos:
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY * 1000))
                .signWith(Keys.hmacShaKeyFor(SIGNING_KEY.getBytes()), SignatureAlgorithm.HS256)
                .compact(); // 3. Devuelve el JWT como string.
    }

    /**
     * <P>Valida si el token es correcto para un usuario.</p>
     * <ul>
     *      <li>1. Obtiene username del token:</li>
     *      <li>2. Compara con el usuario real:</li>
     *      <li>3. Verifica expiración:</li>
     * </ul>
     * <p>Resultado: Retorna true si: El usuario coincide && El token NO está expirado</p>
     */
    public Boolean validateToken(String token, @NonNull UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


    /**
     * <P>Convierte un JWT en un objeto de autenticación de Spring Security.</p>
     * <ul>
     *      <li>1. Parsear token</li>
     *      <li>2. Obtener claims</li>
     *      <li>3. Obtener roles</li>
     *      <li>4. Convertir a objetos GrantedAuthority</li>
     *      <li>5. Crear autenticación</li>
     * </ul>
     * <p>Se genera un objeto que Spring Security puede usar para:</p>
     *  <li>Autorizar endpoints</li>
     *  <li>Identificar al usuario autenticado</li>
     */
    UsernamePasswordAuthenticationToken getAuthenticationToken(final String token, final Authentication existingAuth, final UserDetails userDetails) {

        final JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(SIGNING_KEY.getBytes()).build();

        final Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);

        final Claims claims = claimsJws.getBody();

        final Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }
}
