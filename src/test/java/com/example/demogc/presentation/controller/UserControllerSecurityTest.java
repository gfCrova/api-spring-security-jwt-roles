package com.example.demogc.presentation.controller;

import com.example.demogc.application.dto.UserResponseDTO;
import com.example.demogc.application.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void userCanAccessOwnProfileEndpoint() throws Exception {
        UserResponseDTO response = new UserResponseDTO();
        response.setId(9L);
        response.setUsername("regular-user");
        response.setRoles(Set.of("USER"));

        when(userService.getUserByUsername("regular-user")).thenReturn(response);

        mockMvc.perform(get("/api/users/me")
                        .with(user("regular-user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("regular-user"));

        verify(userService).getUserByUsername("regular-user");
    }

    @Test
    void userCannotAccessAdminUsersListing() throws Exception {
        mockMvc.perform(get("/api/users")
                        .with(user("regular-user").roles("USER")))
                .andExpect(status().isForbidden());
    }
}
