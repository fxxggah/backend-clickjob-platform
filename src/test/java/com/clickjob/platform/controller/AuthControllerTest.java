package com.clickjob.platform.controller;

import com.clickjob.platform.config.SecurityConfig;
import com.clickjob.platform.config.security.JwtAuthenticationFilter;
import com.clickjob.platform.dto.request.LoginRequest;
import com.clickjob.platform.dto.response.AuthResponse;
import com.clickjob.platform.service.AuthService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AuthController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = { SecurityConfig.class, JwtAuthenticationFilter.class }
        )
)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;   // o mesmo mapper (Jackson 3) que a aplicação usa

    @MockitoBean
    private AuthService authService;

    @Test
    @DisplayName("Deve realizar login com sucesso")
    void deveRealizarLoginComSucesso() throws Exception {

        LoginRequest request = LoginRequest.builder()
                .email("gabriel@gmail.com")
                .password("123456")
                .build();

        AuthResponse expectedResponse = AuthResponse.builder()
                .token("jwt-token")
                .type("Bearer")
                .userId(1L)
                .name("Gabriel")
                .email(request.getEmail())
                .role("EMPLOYER")
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.email").value(request.getEmail()))
                .andExpect(jsonPath("$.name").value("Gabriel"))
                .andExpect(jsonPath("$.role").value("EMPLOYER"));

        verify(authService, times(1)).login(any(LoginRequest.class));
    }
}