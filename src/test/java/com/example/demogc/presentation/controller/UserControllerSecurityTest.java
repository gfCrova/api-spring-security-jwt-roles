package com.example.demogc.presentation.controller;

import com.example.demogc.application.dto.UserResponseDTO;
import com.example.demogc.application.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
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

    @Test
    void userCanUpdateOwnProfile() throws Exception {
        UserResponseDTO response = new UserResponseDTO();
        response.setUsername("regular-user");
        response.setEmail("new@example.com");

        when(userService.updateOwnProfile(org.mockito.ArgumentMatchers.eq("regular-user"), org.mockito.ArgumentMatchers.any()))
                .thenReturn(response);

        mockMvc.perform(patch("/api/users/me")
                        .with(user("regular-user").roles("USER"))
                        .contentType("application/json")
                        .content("""
                                {
                                  "email": "new@example.com",
                                  "name": "Regular User",
                                  "phone": 123456789,
                                  "businessTitle": "Developer"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    @Test
    void userCanChangeOwnPassword() throws Exception {
        mockMvc.perform(patch("/api/users/me/password")
                        .with(user("regular-user").roles("USER"))
                        .contentType("application/json")
                        .content("""
                                {
                                  "currentPassword": "oldPassword123",
                                  "newPassword": "newPassword123"
                                }
                                """))
                .andExpect(status().isNoContent());
    }

    @Test
    void userCanDeleteOwnAccount() throws Exception {
        mockMvc.perform(delete("/api/users/me")
                        .with(user("regular-user").roles("USER")))
                .andExpect(status().isNoContent());
    }
}
