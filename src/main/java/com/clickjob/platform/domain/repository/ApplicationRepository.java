package com.clickjob.platform.domain.repository;

import com.clickjob.platform.domain.enums.ApplicationStatus;
import com.clickjob.platform.domain.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByJobId(Long jobId);

    List<Application> findByFreelancerId(Long freelancerId);

    Optional<Application> findByJobIdAndFreelancerId(Long jobId, Long freelancerId);

    List<Application> findByJobIdAndStatus(Long jobId, ApplicationStatus status);
}