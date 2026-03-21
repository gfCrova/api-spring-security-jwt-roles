package com.example.demogc.controller;

import com.example.demogc.config.security.TokenProvider;
import com.example.demogc.dto.AuthToken;
import com.example.demogc.dto.UserLoginDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider jwtTokenUtil;

    public AuthController(AuthenticationManager authenticationManager,
                          TokenProvider jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/authentication")
    public ResponseEntity<AuthToken> login(@Valid @RequestBody UserLoginDTO loginUser) throws AuthenticationException {


            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginUser.getUsername(),
                            loginUser.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtTokenUtil.generateToken(authentication);

            return ResponseEntity.ok(new AuthToken(token));
    }
}
