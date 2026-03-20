package com.example.demogc.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <h5>Punto de entrada para manejar errores de autenticación no autorizada.</h5>
 *
 * <p>Esta clase se ejecuta cuando un usuario intenta acceder a un recurso protegido
 * sin estar autenticado o con credenciales inválidas.</p>
 *
 * <p>Implementa AuthenticationEntryPoint, que es parte de Spring Security
 * y define cómo responder ante intentos de acceso no autorizados.</p>
 */
@Component
public class UnauthorizedEntryPoint implements AuthenticationEntryPoint, Serializable {

    // Este metodo se dispara cuando:
    // ❌ Usuario NO autenticado intenta acceder a endpoint protegido
    @Override
    public void commence(@NonNull HttpServletRequest request,
                         @NonNull HttpServletResponse response,
                         @NonNull AuthenticationException authException) throws IOException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", new Date());
        data.put("status", HttpStatus.UNAUTHORIZED.value());
        data.put("message", "Access Denied, you do not have the necessary permissions to access!");
        data.put("path", request.getRequestURL().toString());
        data.put("pd", "Unauthorized!");

        OutputStream out = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, data);
        out.flush();
    }
}
