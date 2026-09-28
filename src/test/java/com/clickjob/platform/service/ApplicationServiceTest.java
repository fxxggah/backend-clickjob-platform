package com.clickjob.platform.service;

import com.clickjob.platform.domain.enums.ApplicationStatus;
import com.clickjob.platform.domain.enums.JobStatus;
import com.clickjob.platform.domain.enums.UserType;
import com.clickjob.platform.domain.model.Application;
import com.clickjob.platform.domain.model.Job;
import com.clickjob.platform.domain.model.User;
import com.clickjob.platform.domain.repository.ApplicationRepository;
import com.clickjob.platform.dto.response.ApplicationResponse;
import com.clickjob.platform.exception.BusinessException;
import com.clickjob.platform.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private JobService jobService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ApplicationService applicationService;

    // CRUD Testes

    @Test
    @DisplayName("Deve registrar a candidatura de um freelancer em um job com sucesso")
    void deveCriarUmaAplicacaoDeUmFreelancerEmUmJob() {

        User freelancer = User.builder()
                .id(1L)
                .name("Joao")
                .email("joao123@gmail.com")
                .password("joao123")
                .userType(UserType.FREELANCER)
                .build();

        User employer = User.builder()
                .id(2L)
                .name("Gabriel")
                .email("gabriel@gmail.com")
                .password("gabriel")
                .userType(UserType.EMPLOYER)
                .build();

        Job job = Job.builder()
                .id(1L)
                .title("Dev Java")
                .description("Criar uma API REST")
                .status(JobStatus.OPEN)
                .employer(employer)
                .budget(new BigDecimal("5000"))
                .createdAt(LocalDateTime.now())
                .build();

        Application application = Application.builder()
                .job(job)
                .freelancer(freelancer)
                .status(ApplicationStatus.PENDING)
                .build();

        when(jobService.getEntityById(job.getId())).thenReturn(job);
        when(userService.getEntityById(freelancer.getId())).thenReturn(freelancer);
        when(applicationRepository.findByJobIdAndFreelancerId(job.getId(), freelancer.getId())).thenReturn(Optional.empty());
        when(applicationRepository.save(any(Application.class))).thenReturn(application);

        ApplicationResponse result = applicationService.apply(job.getId(), freelancer.getId());


        assertNotNull(result);
        assertEquals(ApplicationStatus.PENDING, result.getStatus());

        verify(jobService, times(1)).getEntityById(job.getId());
        verify(userService, times(1)).getEntityById(freelancer.getId());
        verify(applicationRepository, times(1)).findByJobIdAndFreelancerId(job.getId(), freelancer.getId());
        verify(applicationRepository, times(1)).save(any(Application.class));

    }

    @Test
    @DisplayName("Deve listar todos os freelancers que se candidataram a um job específico")
    void deveRetornarUmaListaDeAplicantesDeUmJob() {

        User freelancerOne = User.builder()
                .id(1L)
                .name("Joao")
                .email("joao123@gmail.com")
                .password("joao123")
                .userType(UserType.FREELANCER)
                .build();

        User freelancerTwo = User.builder()
                .id(2L)
                .name("Eloa")
                .email("eloa123@gmail.com")
                .password("eloa123")
                .userType(UserType.FREELANCER)
                .build();

        User employer = User.builder()
                .id(3L)
                .name("Gabriel")
                .email("gabriel@gmail.com")
                .password("gabriel")
                .userType(UserType.EMPLOYER)
                .build();

        Job job = Job.builder()
                .id(1L)
                .title("Dev Java")
                .description("Criar uma API REST")
                .status(JobStatus.OPEN)
                .employer(employer)
                .budget(new BigDecimal("5000"))
                .createdAt(LocalDateTime.now())
                .build();

        Application applicationOne = Application.builder()
                .job(job)
                .freelancer(freelancerOne)
                .status(ApplicationStatus.PENDING)
                .build();

        Application applicationTwo = Application.builder()
                .job(job)
                .freelancer(freelancerTwo)
                .status(ApplicationStatus.PENDING)
                .build();

        List<Application> applications = List.of(applicationOne, applicationTwo);

        when(applicationRepository.findByJobId(job.getId())).thenReturn(applications);

        List<ApplicationResponse> result = applicationService.findByJob(job.getId());

        assertNotNull(result);
        assertEquals(2, result.size(), "A lista deve conter 2 candidaturas");

        assertEquals("Joao", result.get(0).getFreelancerName());
        assertEquals("Eloa", result.get(1).getFreelancerName());

        verify(applicationRepository, times(1)).findByJobId(job.getId());

    }

    @Test
    @DisplayName("Deve buscar o histórico completo de candidaturas de um freelancer")
    void deveRetornarTodasAplicacoesDeUmFreelancer() {

        User freelancer = User.builder()
                .id(1L)
                .name("Joao")
                .email("joao123@gmail.com")
                .password("joao123")
                .userType(UserType.FREELANCER)
                .build();

        User employer = User.builder()
                .id(3L)
                .name("Gabriel")
                .email("gabriel@gmail.com")
                .password("gabriel")
                .userType(UserType.EMPLOYER)
                .build();

        Job jobOne = Job.builder()
                .id(1L)
                .title("Dev Java Junior")
                .description("Criar e manter uma API REST")
                .status(JobStatus.OPEN)
                .employer(employer)
                .budget(new BigDecimal("5000"))
                .createdAt(LocalDateTime.now())
                .build();

        Job jobTwo = Job.builder()
                .id(2L)
                .title("Dev Java Senior")
                .description("Liderar equipe de Back-end")
                .status(JobStatus.OPEN)
                .employer(employer)
                .budget(new BigDecimal("18000"))
                .createdAt(LocalDateTime.now())
                .build();

        Application applicationOne = Application.builder()
                .job(jobOne)
                .freelancer(freelancer)
                .status(ApplicationStatus.PENDING)
                .build();

        Application applicationTwo = Application.builder()
                .job(jobTwo)
                .freelancer(freelancer)
                .status(ApplicationStatus.PENDING)
                .build();

        List<Application> applications = List.of(applicationOne, applicationTwo);

        when(applicationRepository.findByFreelancerId(freelancer.getId())).thenReturn(applications);

        List<ApplicationResponse> result = applicationService.findByFreelancer(freelancer.getId());

        assertNotNull(result);
        assertEquals(2, result.size(), "Deveria retornar 2 candidaturas");

        assertEquals("Dev Java Junior", result.get(0).getJobTitle());
        assertEquals("Dev Java Senior", result.get(1).getJobTitle());

        verify(applicationRepository, times(1)).findByFreelancerId(1L);

    }

    @Test
    @DisplayName("Deve alterar o status de uma candidatura para aprovado ou reprovado")
    void deveAtualizarStatusDeUmaAplicacao() {

        User freelancer = User.builder()
                .id(1L)
                .name("Joao")
                .email("joao123@gmail.com")
                .password("joao123")
                .userType(UserType.FREELANCER)
                .build();

        User employer = User.builder()
                .id(2L)
                .name("Gabriel")
                .email("gabriel@gmail.com")
                .password("gabriel")
                .userType(UserType.EMPLOYER)
                .build();

        Job job = Job.builder()
                .id(1L)
                .title("Dev Java")
                .description("Criar uma API REST")
                .status(JobStatus.OPEN)
                .employer(employer)
                .budget(new BigDecimal("5000"))
                .createdAt(LocalDateTime.now())
                .build();

        Application application = Application.builder()
                .id(1L)
                .job(job)
                .freelancer(freelancer)
                .status(ApplicationStatus.PENDING)
                .build();

        when(applicationRepository.findById(application.getId())).thenReturn(Optional.of(application));
        when(applicationRepository.save(application)).thenReturn(application);

        ApplicationResponse result = applicationService.updateStatus(application.getId() ,ApplicationStatus.ACCEPTED);

        assertNotNull(result);
        assertEquals(ApplicationStatus.ACCEPTED, result.getStatus());

        assertEquals(1L, result.getId());

        verify(applicationRepository, times(1)).findById(application.getId());
        verify(applicationRepository, times(1)).save(any(Application.class));

    }

    @Test
    @DisplayName("Deve remover uma aplicacao pelo seu ID")
    void deveDeletarUmaAplicacaoPeloSeuID() {

        User freelancer = User.builder()
                .id(1L)
                .name("Joao")
                .email("joao123@gmail.com")
                .password("joao123")
                .userType(UserType.FREELANCER)
                .build();

        User employer = User.builder()
                .id(2L)
                .name("Gabriel")
                .email("gabriel@gmail.com")
                .password("gabriel")
                .userType(UserType.EMPLOYER)
                .build();

        Job job = Job.builder()
                .id(1L)
                .title("Dev Java")
                .description("Criar uma API REST")
                .status(JobStatus.OPEN)
                .employer(employer)
                .budget(new BigDecimal("5000"))
                .createdAt(LocalDateTime.now())
                .build();

        Application application = Application.builder()
                .id(1L)
                .job(job)
                .freelancer(freelancer)
                .status(ApplicationStatus.PENDING)
                .build();

        when(applicationRepository.findById(application.getId())).thenReturn(Optional.of(application));

        applicationService.delete(application.getId());

        verify(applicationRepository, times(1)).findById(application.getId());

        verify(applicationRepository, times(1)).delete(application);

        verifyNoMoreInteractions(applicationRepository);

    }

    // Exceptions Testes

    @Test
    @DisplayName("Deve lancar BusinessException quando aplicacao ja existir")
    void deveLancarExcecaoQuandoAplicacaoJaExiste() {

        User freelancer = User.builder()
                .id(1L)
                .name("Joao")
                .email("joao123@gmail.com")
                .password("joao123")
                .userType(UserType.FREELANCER)
                .build();

        User employer = User.builder()
                .id(2L)
                .name("Gabriel")
                .email("gabriel@gmail.com")
                .password("gabriel")
                .userType(UserType.EMPLOYER)
                .build();

        Job job = Job.builder()
                .id(1L)
                .title("Dev Java")
                .description("Criar uma API REST")
                .status(JobStatus.OPEN)
                .employer(employer)
                .budget(new BigDecimal("5000"))
                .createdAt(LocalDateTime.now())
                .build();

        Application application = Application.builder()
                .id(1L)
                .job(job)
                .freelancer(freelancer)
                .status(ApplicationStatus.PENDING)
                .build();

        when(jobService.getEntityById(job.getId())).thenReturn(job);
        when(userService.getEntityById(freelancer.getId())).thenReturn(freelancer);
        when(applicationRepository.findByJobIdAndFreelancerId(job.getId(), freelancer.getId()))

                .thenReturn(Optional.of(application));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            applicationService.apply(job.getId(), freelancer.getId());
        });

        assertEquals("Você já se candidatou a este job", exception.getMessage());
        verify(applicationRepository, never()).save(any());


    }

    @Test
    @DisplayName("Deve lancar ResourceNotFoundException quando a aplicacao nao existir")
    void deveLancarExcecaoQuandoAplicacaoNaoExitir() {

        Long idInexistente = 1L;

        when(applicationRepository.findById(idInexistente)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            applicationService.updateStatus(idInexistente, ApplicationStatus.ACCEPTED);
        });

        assertEquals("Candidatura com o ID: "+ idInexistente + " não encontrada", exception.getMessage());

        verify(applicationRepository, never()).save(any());
    }
}