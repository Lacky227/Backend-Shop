package com.fullstackfamily.authservice.entity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table (name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String firstName;

    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;


    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role = "ROLE_USER";
}
