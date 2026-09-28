package com.clickjob.platform.dto.response;

import com.clickjob.platform.domain.enums.JobStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class JobResponse {
    private Long id;
    private String title;
    private String description;
    private JobStatus status;
    private BigDecimal budget;
    private Long employerId;
    private String employerName;
    private LocalDateTime createdAt;
    private Integer applications;
}