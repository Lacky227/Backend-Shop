package com.fullstackfamily.notificationservice.dto;

import lombok.Data;

@Data
public class MallingRequest {
    private String templateName;
    private String email;
    private String subject;
    private String unsubscribeLink;
}
