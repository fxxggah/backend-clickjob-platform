package com.clickjob.platform.dto.response;

import com.clickjob.platform.domain.enums.UserType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String aboutMe;
    private UserType userType;
    private LocalDateTime createdAt;
}