package com.example.demogc.config.security;

import com.example.demogc.exception.ApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

@Component
public class UnauthorizedEntryPoint implements AuthenticationEntryPoint, Serializable {

    private final ObjectMapper objectMapper;

    public UnauthorizedEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "Authentication is required to access this resource",
                request.getRequestURI(),
                Map.of()
        );

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
