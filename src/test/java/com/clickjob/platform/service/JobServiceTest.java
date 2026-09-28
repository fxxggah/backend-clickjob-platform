package com.clickjob.platform.service;

import com.clickjob.platform.domain.enums.JobStatus;
import com.clickjob.platform.domain.model.Job;
import com.clickjob.platform.domain.model.User;
import com.clickjob.platform.domain.repository.JobRepository;
import com.clickjob.platform.dto.request.JobRequest;
import com.clickjob.platform.dto.response.JobResponse;
import com.clickjob.platform.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.clickjob.platform.domain.enums.UserType.EMPLOYER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    JobRepository jobRepository;

    @Mock
    UserService userService;

    @InjectMocks
    JobService jobService;

    // CRUD Testes

    @Test
    @DisplayName("Deve criar um novo job com sucesso")
    void deveCriarUmJob() {

        User user = User.builder()
                .id(1L)
                .name("Gabriel")
                .email("gabriel@gmail.com")
                .password("gabriel")
                .userType(EMPLOYER)
                .build();

        when(userService.getEntityById(user.getId())).thenReturn(user);

        JobRequest jobRequest = new JobRequest(
                "Desenvolvedor Java",
                "Desenvolver uma aplicação web",
                new BigDecimal("5000"));

        Job job = Job.builder()
                .title(jobRequest.getTitle())
                .description(jobRequest.getDescription())
                .budget(jobRequest.getBudget())
                .status(JobStatus.OPEN)
                .employer(user)
                .build();

        when(jobRepository.save(any(Job.class))).thenReturn(job);

        JobResponse result = jobService.create(jobRequest, user.getId());

        assertEquals(job.getTitle(), result.getTitle());
        assertEquals(job.getDescription(), result.getDescription());
        assertEquals(job.getBudget(), result.getBudget());
        assertEquals(job.getStatus(), result.getStatus());
        assertEquals(job.getEmployer().getId(), result.getEmployerId());

        verify(jobRepository).save(any(Job.class));
        verify(userService).getEntityById(user.getId());


    }

    @Test
    @DisplayName("Deve retornar Job pelo ID")
    void deveRetornarJobPeloId() {

        User user = User.builder()
                .id(1L)
                .name("Gabriel")
                .email("gabriel@gmail.com")
                .password("gabriel")
                .userType(EMPLOYER)
                .build();

        Job job = Job.builder()
                .id(1L)
                .title("Desenvolvedor Java")
                .description("Desenvolver o back-end de uma aplicação web")
                .budget(new BigDecimal("5000"))
                .status(JobStatus.OPEN)
                .employer(user)
                .build();

        when(jobRepository.findById(job.getId())).thenReturn(Optional.of(job));

        Job result = jobService.getEntityById(job.getId());

        assertEquals(job.getTitle(), result.getTitle());
        assertEquals(job.getDescription(), result.getDescription());
        assertEquals(job.getBudget(), result.getBudget());
        assertEquals(job.getStatus(), result.getStatus());
        assertEquals(job.getEmployer().getId(), result.getEmployer().getId());

        verify(jobRepository).findById(job.getId());


    }

    @Test
    @DisplayName("Deve retornar Job convertido em DTO pelo ID")
    void deveRetornarJobConvertidoEmDTOPeloId() {

        User user = User.builder()
                .id(1L)
                .name("Gabriel")
                .email("gabriel@gmail.com")
                .password("gabriel")
                .userType(EMPLOYER)
                .build();

        Job job = Job.builder()
                .id(1L)
                .title("Desenvolvedor Java")
                .description("Desenvolver o back-end de uma aplicação web")
                .budget(new BigDecimal("5000"))
                .status(JobStatus.OPEN)
                .employer(user)
                .build();


        when(jobRepository.findById(job.getId())).thenReturn(Optional.of(job));

        JobResponse result = jobService.findById(job.getId());

        assertEquals(job.getTitle(), result.getTitle());
        assertEquals(job.getDescription(), result.getDescription());
        assertEquals(job.getBudget(), result.getBudget());
        assertEquals(job.getStatus(), result.getStatus());
        assertEquals(job.getEmployer().getId(), result.getEmployerId());

        verify(jobRepository).findById(job.getId());

    }

    @Test
    @DisplayName("Deve listar todos os Jobs com status OPEN")
    void deveListarTodosOsJobsComStatusOPEN() {

        User user = User.builder()
                .id(1L)
                .name("Gabriel")
                .email("gabriel@gmail.com")
                .password("gabriel")
                .userType(EMPLOYER)
                .build();

        Job jobOne = Job.builder()
                .id(1L)
                .title("Desenvolvedor Java")
                .description("Desenvolver o back-end de uma aplicação web")
                .budget(new BigDecimal("5000"))
                .status(JobStatus.OPEN)
                .employer(user)
                .build();

        Job jobTwo = Job.builder()
                .id(2L)
                .title("Desenvolvedor Next.js")
                .description("Desenvolver o front-end de uma aplicação web")
                .budget(new BigDecimal("4000"))
                .status(JobStatus.OPEN)
                .employer(user)
                .build();

        when(jobRepository.findAllByOrderByCreatedAtDesc(JobStatus.OPEN)).thenReturn(List.of(jobOne, jobTwo));

        List<JobResponse> result = jobService.findAllOpen();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(jobOne.getId(), result.get(0).getId());
        assertEquals(jobTwo.getId(), result.get(1).getId());
        assertEquals(jobOne.getTitle(), result.get(0).getTitle());
        assertEquals(jobTwo.getTitle(), result.get(1).getTitle());
        assertEquals(jobOne.getDescription(), result.get(0).getDescription());
        assertEquals(jobTwo.getDescription(), result.get(1).getDescription());
        assertEquals(jobOne.getBudget(), result.get(0).getBudget());
        assertEquals(jobTwo.getBudget(), result.get(1).getBudget());
        assertEquals(jobOne.getStatus(), result.get(0).getStatus());
        assertEquals(jobTwo.getStatus(), result.get(1).getStatus());
        assertEquals(jobOne.getEmployer().getId(), result.get(0).getEmployerId());
        assertEquals(jobTwo.getEmployer().getId(), result.get(1).getEmployerId());

        verify(jobRepository).findAllByOrderByCreatedAtDesc(JobStatus.OPEN);

    }

    @Test
    @DisplayName("Deve retornar lista de jobs pelo ID do empregador")
    void deveListarTodosOsJobsPeloIdDoEmpregador() {

        User user = User.builder()
                .id(1L)
                .name("Gabriel")
                .email("gabriel@gmail.com")
                .password("gabriel")
                .userType(EMPLOYER)
                .build();

        Job jobOne = Job.builder()
                .id(1L)
                .title("Desenvolvedor Java")
                .description("Desenvolver o back-end de uma aplicação web")
                .budget(new BigDecimal("5000"))
                .status(JobStatus.OPEN)
                .employer(user)
                .build();

        Job jobTwo = Job.builder()
                .id(2L)
                .title("Desenvolvedor Next.js")
                .description("Desenvolver o front-end de uma aplicação web")
                .budget(new BigDecimal("4000"))
                .status(JobStatus.OPEN)
                .employer(user)
                .build();

        when(jobRepository.findByEmployerId(user.getId())).thenReturn(List.of(jobOne, jobTwo));

        List<JobResponse> result = jobService.findByEmployer(user.getId());

        assertEquals(2, result.size());
        assertEquals(jobOne.getId(), result.get(0).getId());
        assertEquals(jobTwo.getId(), result.get(1).getId());
        assertEquals(jobOne.getTitle(), result.get(0).getTitle());
        assertEquals(jobTwo.getTitle(), result.get(1).getTitle());
        assertEquals(jobOne.getDescription(), result.get(0).getDescription());
        assertEquals(jobTwo.getDescription(), result.get(1).getDescription());
        assertEquals(jobOne.getBudget(), result.get(0).getBudget());
        assertEquals(jobTwo.getBudget(), result.get(1).getBudget());
        assertEquals(jobOne.getStatus(), result.get(0).getStatus());
        assertEquals(jobTwo.getStatus(), result.get(1).getStatus());
        assertEquals(jobOne.getEmployer().getId(), result.get(0).getEmployerId());
        assertEquals(jobTwo.getEmployer().getId(), result.get(1).getEmployerId());

        verify(jobRepository).findByEmployerId(user.getId());

    }

    @Test
    @DisplayName("Deve atualizar o status de um job com sucesso")
    void updateStatus() {

        User user = User.builder()
                .id(1L)
                .name("Gabriel")
                .email("gabriel@gmail.com")
                .password("gabriel")
                .userType(EMPLOYER)
                .build();

        Job job = Job.builder()
                .id(1L)
                .title("Desenvolvedor Java")
                .description("Desenvolver o back-end de uma aplicação web")
                .budget(new BigDecimal("5000"))
                .status(JobStatus.OPEN)
                .employer(user)
                .build();

        when(jobRepository.save(any(Job.class))).thenReturn(job);
        when(jobRepository.findById(job.getId())).thenReturn(Optional.of(job));

        JobResponse result = jobService.updateStatus(job.getId(), JobStatus.CLOSED);

        assertEquals(job.getTitle(), result.getTitle());
        assertEquals(job.getDescription(), result.getDescription());
        assertEquals(job.getBudget(), result.getBudget());
        assertEquals(JobStatus.CLOSED, result.getStatus());
        assertEquals(job.getEmployer().getId(), result.getEmployerId());

        verify(jobRepository).findById(job.getId());
        verify(jobRepository).save(any(Job.class));

    }

    @Test
    @DisplayName("Deve deletar um job com sucesso")
    void deveDeletarJobComSucesso() {

        User user = User.builder()
                .id(1L)
                .name("Gabriel")
                .email("gabriel@gmail.com")
                .password("gabriel")
                .userType(EMPLOYER)
                .build();

        Job job = Job.builder()
                .id(1L)
                .title("Desenvolvedor Java")
                .description("Desenvolver o back-end de uma aplicação web")
                .budget(new BigDecimal("5000"))
                .status(JobStatus.OPEN)
                .employer(user)
                .build();

        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));

        jobService.delete(job.getId());

        verify(jobRepository).findById(job.getId());
        verify(jobRepository).delete(job);
    }

    // Exceptions Testes

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando um job nao existir")
    void deveLancarExcecaoQuandoUsuarioNaoExistirPeloId() {

        Long idInexistente = 1L;

        when(jobRepository.findById(idInexistente)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> jobService.getEntityById(idInexistente));

        assertEquals("Job com o ID: " + idInexistente +" não encontrado", exception.getMessage());

        verify(jobRepository).findById(idInexistente);
    }

}