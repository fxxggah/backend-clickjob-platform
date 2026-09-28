package com.clickjob.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.clickjob.platform.config.security.JwtService;
import com.clickjob.platform.domain.enums.JobStatus;
import com.clickjob.platform.dto.request.JobRequest;
import com.clickjob.platform.dto.response.JobResponse;
import com.clickjob.platform.service.JobService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JobController.class)
@AutoConfigureMockMvc(addFilters = false)
class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules();

    @MockitoBean
    private JobService jobService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @DisplayName("Deve criar um novo job com sucesso")
    void deveCriarUmJob() throws Exception {

        Long employerId = 1L;

        JobRequest request = JobRequest.builder()
                .title("Desenvolvedor Java")
                .description("Vaga para desenvolvedor Java com experiência em Spring Boot.")
                .budget(new BigDecimal("5000.00"))
                .build();

        JobResponse response = JobResponse.builder()
                .id(1L)
                .title(request.getTitle())
                .description(request.getDescription())
                .status(JobStatus.OPEN)
                .budget(request.getBudget())
                .employerId(employerId)
                .employerName("Gabriel")
                .createdAt(LocalDateTime.now())
                .applications(0)
                .build();

        when(jobService.create(request, employerId)).thenReturn(response);

        mockMvc.perform(post("/api/jobs")
                        .param("employerId", employerId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.title").value(response.getTitle()))
                .andExpect(jsonPath("$.description").value(response.getDescription()))
                .andExpect(jsonPath("$.status").value(response.getStatus().toString()))
                .andExpect(jsonPath("$.budget").value(response.getBudget().doubleValue()))
                .andExpect(jsonPath("$.employerId").value(response.getEmployerId()))
                .andExpect(jsonPath("$.employerName").value(response.getEmployerName()));

        verify(jobService, times(1)).create(request, employerId);
    }

    @Test
    @DisplayName("Deve retornar um job pelo ID")
    void deveRetornarUmJobPeloId() throws Exception {

        Long employerId = 1L;

        JobResponse response = JobResponse.builder()
                .id(1L)
                .title("Desenvolvedor Java")
                .description("Vaga para desenvolvedor Java com experiência em Spring Boot.")
                .status(JobStatus.OPEN)
                .budget(new BigDecimal("5000.00"))
                .employerId(employerId)
                .employerName("Gabriel")
                .createdAt(LocalDateTime.now())
                .applications(0)
                .build();

        when(jobService.findById(response.getId())).thenReturn(response);

        mockMvc.perform(get("/api/jobs/{id}", response.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.title").value(response.getTitle()))
                .andExpect(jsonPath("$.description").value(response.getDescription()))
                .andExpect(jsonPath("$.status").value(response.getStatus().toString()))
                .andExpect(jsonPath("$.budget").value(response.getBudget().doubleValue()))
                .andExpect(jsonPath("$.employerId").value(response.getEmployerId()))
                .andExpect(jsonPath("$.employerName").value(response.getEmployerName()));

        verify(jobService, times(1)).findById(response.getId());
    }

    @Test
    @DisplayName("Deve listar todos os jobs com status OPEN")
    void deveListarTodosOsJobsComStatusOpen() throws Exception {

        Long employerId = 1L;

        JobResponse responseOne = JobResponse.builder()
                .id(1L)
                .title("Desenvolvedor Java Jr")
                .description("Vaga para desenvolvedor Java com 1 ano de experiência em Spring Boot.")
                .status(JobStatus.OPEN)
                .budget(new BigDecimal("5000.00"))
                .employerId(employerId)
                .employerName("Gabriel")
                .createdAt(LocalDateTime.now())
                .applications(0)
                .build();

        JobResponse responseTwo = JobResponse.builder()
                .id(1L)
                .title("Desenvolvedor Java Sr")
                .description("Vaga para desenvolvedor Java com 5 anos de experiência em Spring Boot.")
                .status(JobStatus.OPEN)
                .budget(new BigDecimal("15000.00"))
                .employerId(employerId)
                .employerName("Gabriel")
                .createdAt(LocalDateTime.now())
                .applications(0)
                .build();

        when(jobService.findAllOpen()).thenReturn(List.of(responseOne, responseTwo));

        mockMvc.perform(get("/api/jobs/open"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(responseOne.getId()))
                .andExpect(jsonPath("$[0].title").value(responseOne.getTitle()))
                .andExpect(jsonPath("$[0].description").value(responseOne.getDescription()))
                .andExpect(jsonPath("$[0].status").value(responseOne.getStatus().toString()))
                .andExpect(jsonPath("$[0].budget").value(responseOne.getBudget().doubleValue()))
                .andExpect(jsonPath("$[0].employerId").value(responseOne.getEmployerId()))
                .andExpect(jsonPath("$[0].employerName").value(responseOne.getEmployerName()))
                .andExpect(jsonPath("$[1].id").value(responseTwo.getId()))
                .andExpect(jsonPath("$[1].title").value(responseTwo.getTitle()))
                .andExpect(jsonPath("$[1].description").value(responseTwo.getDescription()))
                .andExpect(jsonPath("$[1].status").value(responseTwo.getStatus().toString()))
                .andExpect(jsonPath("$[1].budget").value(responseTwo.getBudget().doubleValue()))
                .andExpect(jsonPath("$[1].employerId").value(responseTwo.getEmployerId()))
                .andExpect(jsonPath("$[1].employerName").value(responseTwo.getEmployerName()));

        verify(jobService, times(1)).findAllOpen();

    }

    @Test
    @DisplayName("Deve listar todos os jobs de um empregador específico")
    void deveListarTodosOsJobsDeUmEmployer() throws Exception {

        Long employerId = 1L;

        JobResponse responseOne = JobResponse.builder()
                .id(1L)
                .title("Desenvolvedor Java Jr")
                .description("Vaga para desenvolvedor Java com 1 ano de experiência em Spring Boot.")
                .status(JobStatus.OPEN)
                .budget(new BigDecimal("5000.00"))
                .employerId(employerId)
                .employerName("Gabriel")
                .createdAt(LocalDateTime.now())
                .applications(0)
                .build();

        JobResponse responseTwo = JobResponse.builder()
                .id(1L)
                .title("Desenvolvedor Java Sr")
                .description("Vaga para desenvolvedor Java com 5 anos de experiência em Spring Boot.")
                .status(JobStatus.OPEN)
                .budget(new BigDecimal("15000.00"))
                .employerId(employerId)
                .employerName("Gabriel")
                .createdAt(LocalDateTime.now())
                .applications(0)
                .build();

        when(jobService.findByEmployer(employerId)).thenReturn(List.of(responseOne, responseTwo));

        mockMvc.perform(get("/api/jobs/employer/{employerId}", employerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(responseOne.getId()))
                .andExpect(jsonPath("$[0].title").value(responseOne.getTitle()))
                .andExpect(jsonPath("$[0].description").value(responseOne.getDescription()))
                .andExpect(jsonPath("$[0].status").value(responseOne.getStatus().toString()))
                .andExpect(jsonPath("$[0].budget").value(responseOne.getBudget().doubleValue()))
                .andExpect(jsonPath("$[0].employerId").value(responseOne.getEmployerId()))
                .andExpect(jsonPath("$[0].employerName").value(responseOne.getEmployerName()))
                .andExpect(jsonPath("$[1].id").value(responseTwo.getId()))
                .andExpect(jsonPath("$[1].title").value(responseTwo.getTitle()))
                .andExpect(jsonPath("$[1].description").value(responseTwo.getDescription()))
                .andExpect(jsonPath("$[1].status").value(responseTwo.getStatus().toString()))
                .andExpect(jsonPath("$[1].budget").value(responseTwo.getBudget().doubleValue()))
                .andExpect(jsonPath("$[1].employerId").value(responseTwo.getEmployerId()))
                .andExpect(jsonPath("$[1].employerName").value(responseTwo.getEmployerName()));

        verify(jobService, times(1)).findByEmployer(employerId);

    }

    @Test
    @DisplayName("Deve atualizar o status de um job pelo ID")
    void deveAtualizarOStatusDeUmJobPeloID() throws Exception {

        Long jobId = 1L;

        JobResponse response = JobResponse.builder()
                .id(jobId)
                .title("Desenvolvedor Java")
                .description("Vaga para desenvolvedor Java com Spring Boot")
                .status(JobStatus.CLOSED)
                .budget(new BigDecimal("5000.00"))
                .employerId(1L)
                .employerName("Gabriel")
                .createdAt(LocalDateTime.now())
                .applications(0)
                .build();

        when(jobService.updateStatus(jobId, JobStatus.CLOSED))
                .thenReturn(response);

        mockMvc.perform(patch("/api/jobs/{id}/status", jobId)
                        .param("status", "CLOSED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.status").value(response.getStatus().toString()));

        verify(jobService, times(1)).updateStatus(jobId, JobStatus.CLOSED);
    }

    @Test
    @DisplayName("Deve deletar um job pelo ID")
    void deveDeletarUmJobPeloId() throws Exception {

        doNothing().when(jobService).delete(1L);

        mockMvc.perform(delete("/api/jobs/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(jobService, times(1)).delete(1L);

    }
}