package com.fullstackfamily.notificationservice.dto;

import lombok.Data;

@Data
public class MallingRequest {
    private String email;
    private String subject;
}
