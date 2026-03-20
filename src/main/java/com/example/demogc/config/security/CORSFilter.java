package com.example.demogc.config.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * <h5>Filtro personalizado para manejar CORS (Cross-Origin Resource Sharing).</h5>
 *
 * <p>Este filtro intercepta todas las requests HTTP y agrega headers
 * necesarios para permitir comunicación entre distintos orígenes
 * (por ejemplo: frontend en React y backend en Spring Boot).</p>
 *
 * <p>Implementa la interfaz javax.servlet.Filter, lo que permite
 * intervenir en el ciclo de vida de cada request.</p>
 */
@Component
public class CORSFilter implements Filter {

    // Metodo de inicialización del filtro.
    // Se ejecuta una sola vez cuando el servidor arranca.
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    // Se ejecuta en cada request HTTP.
    // Permite modificar request/response antes de que lleguen al controller.
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        // Convierte la respuesta genérica en HTTP para poder modificar headers.
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // Permite requests desde el dominio, en este caso (http://localhost:3000).
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        // Permite cookies, headers de autenticación
        response.setHeader("Access-Control-Allow-Credentials", "true");
        // Define qué métodos acepta el backend.
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
        // Tiempo (segundos) que el navegador puede cachear la respuesta CORS.
        response.setHeader("Access-Control-Max-Age", "3600");
        // Define qué headers puede enviar el cliente.
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with, Content-Type, Authorization, Origin, Accept, Access-Control-Request-Method, Access-Control-Request-Method, Access-Control-Request-Headers");

        filterChain.doFilter(servletRequest, servletResponse);
    }

    // Se ejecuta cuando el servidor se apaga. Libera recursos (si hubiera).
    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
