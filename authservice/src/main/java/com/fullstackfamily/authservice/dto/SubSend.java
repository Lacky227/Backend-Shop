package com.fullstackfamily.authservice.dto;

import lombok.Data;

@Data
public class SubSend {
    private String email;
    private boolean isSubscribed;
}
