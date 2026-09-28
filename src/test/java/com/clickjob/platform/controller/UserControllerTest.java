package com.clickjob.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.clickjob.platform.config.security.JwtService;
import com.clickjob.platform.domain.enums.UserType;
import com.clickjob.platform.dto.request.RegisterRequest;
import com.clickjob.platform.dto.response.UserResponse;
import com.clickjob.platform.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @DisplayName("Deve criar um novo usuário com sucesso")
    void deveCriarUmUsuario() throws Exception {

        RegisterRequest request = RegisterRequest.builder()
                .name("Gabriel")
                .email("gabriel@gmail.com")
                .password("gabriel")
                .userType(UserType.EMPLOYER)
                .build();

        UserResponse response = UserResponse.builder()
                .id(1L)
                .name(request.getName())
                .email(request.getEmail())
                .userType(request.getUserType())
                .createdAt(LocalDateTime.now())
                .build();

        when(userService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId().intValue()))
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andExpect(jsonPath("$.email").value(response.getEmail()))
                .andExpect(jsonPath("$.userType").value(response.getUserType().name()));

        verify(userService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("Deve retornar um usuário pelo ID")
    void deveRetornarUmUsuarioPeloId() throws Exception {

        Long id = 1L;

        UserResponse response = UserResponse.builder()
                .id(id)
                .name("Gabriel")
                .email("gabriel@gmail.com")
                .userType(UserType.EMPLOYER)
                .createdAt(LocalDateTime.now())
                .build();

        when(userService.findById(id)).thenReturn(response);

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId().intValue()))
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andExpect(jsonPath("$.email").value(response.getEmail()))
                .andExpect(jsonPath("$.userType").value(response.getUserType().name()));

        verify(userService, times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve retornar todos os usuários freelancers")
    void deveRetornarTodosOsUsuariosFreelancers() throws Exception {

        UserResponse responseOne = UserResponse.builder()
                .id(1L)
                .name("Gabriel")
                .email("gabriel@gmail.com")
                .userType(UserType.FREELANCER)
                .createdAt(LocalDateTime.now())
                .build();

        UserResponse responseTwo = UserResponse.builder()
                .id(2L)
                .name("Joao")
                .email("joao@gmail.com")
                .userType(UserType.FREELANCER)
                .createdAt(LocalDateTime.now())
                .build();

        when(userService.findAllFreelancers()).thenReturn(List.of(responseOne, responseTwo));

        mockMvc.perform(get("/api/users/freelancers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(responseOne.getId().intValue()))
                .andExpect(jsonPath("$[0].name").value(responseOne.getName()))
                .andExpect(jsonPath("$[0].email").value(responseOne.getEmail()))
                .andExpect(jsonPath("$[0].userType").value(responseOne.getUserType().name()))
                .andExpect(jsonPath("$[1].id").value(responseTwo.getId().intValue()))
                .andExpect(jsonPath("$[1].name").value(responseTwo.getName()))
                .andExpect(jsonPath("$[1].email").value(responseTwo.getEmail()))
                .andExpect(jsonPath("$[1].userType").value(responseTwo.getUserType().name()));

        verify(userService, times(1)).findAllFreelancers();
    }

    @Test
    @DisplayName("Deve deletar um usuário pelo ID")
    void deveDeletarUsuarioPeloId() throws Exception {

        doNothing().when(userService).delete(any(Long.class));

        mockMvc.perform(delete("/api/users/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).delete(any(Long.class));

        verify(userService, times(1)).delete(1L);

    }
}