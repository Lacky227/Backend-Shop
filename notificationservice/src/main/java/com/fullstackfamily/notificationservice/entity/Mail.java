package com.fullstackfamily.notificationservice.entity;

import lombok.Data;

import java.util.List;

@Data
public class Mail {
    private List<String> to;
    private String subject;
    private String body;
}
