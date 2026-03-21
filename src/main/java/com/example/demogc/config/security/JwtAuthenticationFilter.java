package com.example.demogc.config.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final UserDetailsService userDetailsService;
    private final TokenProvider tokenProvider;
    private final String headerName;
    private final String tokenPrefix;

    public JwtAuthenticationFilter(
            UserDetailsService userDetailsService,
            TokenProvider tokenProvider,
            @Value("${jwt.header.string}") String headerName,
            @Value("${jwt.token.prefix}") String tokenPrefix
    ) {
        this.userDetailsService = userDetailsService;
        this.tokenProvider = tokenProvider;
        this.headerName = headerName;
        this.tokenPrefix = tokenPrefix + " ";
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String header = request.getHeader(headerName);

        if (header == null || !header.startsWith(tokenPrefix)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authToken = header.substring(tokenPrefix.length()).trim();

        try {
            String username = tokenProvider.getUsernameFromToken(authToken);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (tokenProvider.validateToken(authToken, userDetails)) {
                    var authentication = tokenProvider.getAuthenticationToken(authToken, userDetails);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (JwtException | IllegalArgumentException ex) {
            SecurityContextHolder.clearContext();
            LOGGER.warn("Invalid JWT token for request {}", request.getRequestURI());
        }

        filterChain.doFilter(request, response);
    }
}
