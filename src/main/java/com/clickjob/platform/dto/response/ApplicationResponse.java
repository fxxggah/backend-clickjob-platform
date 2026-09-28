package com.clickjob.platform.dto.response;

import com.clickjob.platform.domain.enums.ApplicationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ApplicationResponse {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private Long freelancerId;
    private String freelancerName;
    private ApplicationStatus status;
    private LocalDateTime createdAt;
}