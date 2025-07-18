package com.fullstackfamily.authservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "blackListToken")
public class BlackListToken {
    @Id
    private String token;
    private Date expiryDate;
}
