package com.clickjob.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.clickjob.platform.config.security.JwtService;
import com.clickjob.platform.domain.enums.ApplicationStatus;
import com.clickjob.platform.dto.response.ApplicationResponse;
import com.clickjob.platform.service.ApplicationService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApplicationController.class)
@AutoConfigureMockMvc(addFilters = false)
class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules();

    @MockitoBean
    private ApplicationService applicationService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @DisplayName("Deve aplicar um freelancer em um job")
    void deveAplicarUmFreelancerEmUmJob() throws Exception {

        Long jobId = 1L;

        Long freelancerId = 1L;

        ApplicationResponse response = ApplicationResponse.builder()
                .id(1L)
                .jobId(jobId)
                .jobTitle("Desenvolvedor Java")
                .freelancerId(freelancerId)
                .freelancerName("Gabriel")
                .status(ApplicationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        when(applicationService.apply(jobId, freelancerId)).thenReturn(response);

        mockMvc.perform(post("/api/applications")
                .param("jobId", jobId.toString())
                .param("freelancerId", freelancerId.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.jobId").value(jobId))
                .andExpect(jsonPath("$.freelancerId").value(freelancerId))
                .andExpect(jsonPath("$.jobTitle").value("Desenvolvedor Java"))
                .andExpect(jsonPath("$.freelancerName").value("Gabriel"));

        verify(applicationService, times(1)).apply(jobId, freelancerId);

    }

    @Test
    @DisplayName("Deve listar todas as aplicações de um job")
    void deveListarTodasAplicacoesDeUmJob() throws Exception {

        Long freelancerOneId = 1L;

        Long freelancerTwoId = 2L;

        Long jobId = 1L;

        ApplicationResponse responseOne = ApplicationResponse.builder()
                .id(1L)
                .jobId(jobId)
                .jobTitle("Desenvolvedor Java")
                .freelancerId(freelancerOneId)
                .freelancerName("Gabriel")
                .status(ApplicationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        ApplicationResponse responseTwo = ApplicationResponse.builder()
                .id(1L)
                .jobId(jobId)
                .jobTitle("Desenvolvedor Java")
                .freelancerId(freelancerTwoId)
                .freelancerName("Gabriel")
                .status(ApplicationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        when(applicationService.findByJob(jobId)).thenReturn(List.of(responseOne, responseTwo));

        mockMvc.perform(get("/api/applications/job/{jobId}", jobId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].jobId").value(jobId))
                .andExpect(jsonPath("$[0].freelancerId").value(freelancerOneId))
                .andExpect(jsonPath("$[1].jobId").value(jobId))
                .andExpect(jsonPath("$[1].freelancerId").value(freelancerTwoId));

        verify(applicationService, times(1)).findByJob(jobId);
    }

    @Test
    @DisplayName("Deve listar todas as aplicações de um freelancer")
    void DeveListarTodasAsAplicacoesDeUmFreelancer() throws Exception {

        Long freelancerId = 1L;

        Long jobOneId = 1L;

        Long jobTwoId = 2L;

        ApplicationResponse responseOne = ApplicationResponse.builder()
                .id(1L)
                .jobId(jobOneId)
                .jobTitle("Desenvolvedor Java Jr")
                .freelancerId(freelancerId)
                .freelancerName("Gabriel")
                .status(ApplicationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        ApplicationResponse responseTwo = ApplicationResponse.builder()
                .id(2L)
                .jobId(jobTwoId)
                .jobTitle("Desenvolvedor Java Sr")
                .freelancerId(freelancerId)
                .freelancerName("Gabriel")
                .status(ApplicationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        when(applicationService.findByFreelancer(freelancerId)).thenReturn(List.of(responseOne, responseTwo));

        mockMvc.perform(get("/api/applications/freelancer/{freelancerId}", freelancerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].freelancerId").value(freelancerId))
                .andExpect(jsonPath("$[0].jobId").value(jobOneId))
                .andExpect(jsonPath("$[1].freelancerId").value(freelancerId))
                .andExpect(jsonPath("$[1].jobId").value(jobTwoId));

        verify(applicationService, times(1)).findByFreelancer(freelancerId);
    }

    @Test
    @DisplayName("Deve atualizar o status de uma aplicação")
    void deveAtualizarOStatusDeUmaAplicacao() throws Exception {

        Long freelancerId = 1L;

        Long jobId = 1L;

        ApplicationResponse response = ApplicationResponse.builder()
                .id(1L)
                .jobId(jobId)
                .jobTitle("Desenvolvedor Java")
                .freelancerId(freelancerId)
                .freelancerName("Gabriel")
                .status(ApplicationStatus.ACCEPTED)
                .createdAt(LocalDateTime.now())
                .build();

        when(applicationService.updateStatus(1L, ApplicationStatus.ACCEPTED)).thenReturn(response);

        mockMvc.perform(patch("/api/applications/{id}/status", 1L)
                .param("status", ApplicationStatus.ACCEPTED.name())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.jobId").value(jobId))
                .andExpect(jsonPath("$.freelancerId").value(freelancerId))
                .andExpect(jsonPath("$.status").value(ApplicationStatus.ACCEPTED.name()));

        verify(applicationService, times(1)).updateStatus(1L, ApplicationStatus.ACCEPTED);
    }

    @Test
    @DisplayName("Deve deletar uma aplicação")
    void deveDeletarUmaAplicacao() throws Exception {

        doNothing().when(applicationService).delete(1L);

        mockMvc.perform(delete("/api/applications/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(applicationService, times(1)).delete(1L);
    }
}