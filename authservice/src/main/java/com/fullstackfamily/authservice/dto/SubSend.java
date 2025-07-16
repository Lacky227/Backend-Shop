package com.fullstackfamily.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubSend {
    private String email;
    private boolean isSubscribed;
}
