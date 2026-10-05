package com.clickjob.platform.dto.request;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String phone;
    private String aboutMe;
}