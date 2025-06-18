package com.fullstackfamily.notificationservice.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "subscribers")
public class Subscriber {
    @Id
    private String id;
    private String email;
}
