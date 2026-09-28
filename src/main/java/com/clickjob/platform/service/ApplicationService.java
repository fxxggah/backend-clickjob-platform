package com.clickjob.platform.service;

import com.clickjob.platform.domain.enums.ApplicationStatus;
import com.clickjob.platform.domain.model.Application;
import com.clickjob.platform.domain.model.Job;
import com.clickjob.platform.domain.model.User;
import com.clickjob.platform.domain.repository.ApplicationRepository;
import com.clickjob.platform.dto.response.ApplicationResponse;
import com.clickjob.platform.exception.BusinessException;
import com.clickjob.platform.exception.ResourceNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobService jobService;
    private final UserService userService;

    public ApplicationService(ApplicationRepository applicationRepository, JobService jobService, UserService userService) {
        this.applicationRepository = applicationRepository;
        this.jobService = jobService;
        this.userService = userService;
    }

    @Transactional
    public ApplicationResponse apply(Long jobId, Long freelancerId) {

        Job job = jobService.getEntityById(jobId);

        User freelancer = userService.getEntityById(freelancerId);

        if (applicationRepository.findByJobIdAndFreelancerId(jobId, freelancerId).isPresent()) {
            throw new BusinessException("Você já se candidatou a este job");
        }

        Application application = Application.builder()
                .job(job)
                .freelancer(freelancer)
                .status(ApplicationStatus.PENDING)
                .build();

        application = applicationRepository.save(application);

        return toResponse(application);
    }

    public List<ApplicationResponse> findByJob(Long jobId) {
        jobService.getEntityById(jobId); // Garante que o job existe antes de buscar as candidaturas
        return applicationRepository.findByJobId(jobId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ApplicationResponse> findByFreelancer(Long freelancerId) {
        userService.getEntityById(freelancerId); // Garante que o usuário existe
        return applicationRepository.findByFreelancerId(freelancerId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Application getEntityById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidatura com o ID: " + id + " não encontrada"));
    }

    @Transactional
    public ApplicationResponse updateStatus(Long applicationId, ApplicationStatus status) {
        Application application = getEntityById(applicationId);

        application.setStatus(status);
        application = applicationRepository.save(application);

        return toResponse(application);
    }

    private ApplicationResponse toResponse(@NotNull Application application) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .jobId(application.getJob().getId())
                .jobTitle(application.getJob().getTitle())
                .freelancerId(application.getFreelancer().getId())
                .freelancerName(application.getFreelancer().getName())
                .status(application.getStatus())
                .createdAt(application.getCreatedAt())
                .build();
    }

    @Transactional
    public void delete(Long id) {
        Application application = getEntityById(id);

        applicationRepository.delete(application);
    }

}