package com.fullstackfamily.authservice.service;

import com.fullstackfamily.authservice.dto.*;
import com.fullstackfamily.authservice.entity.AuthProvider;
import com.fullstackfamily.authservice.entity.User;
import com.fullstackfamily.authservice.repository.UserRepository;
import com.fullstackfamily.authservice.validation.ValidationUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SubServiceSender subServiceSender;
    @Value("${google.client-id}")
    private String clientId;

    public ResponseEntity<?> registerUser(RegisterRequest request) {
        if (ValidationUtils.firstNameInvalid(request.getFirstName())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Ім’я є обов’язковим. Має містити від 1 до 15 символів."));
        } else if (ValidationUtils.lastNameInvalid(request.getLastName())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Прізвище не коректне. Має містити від 1 до 15 символів."));
        }

        if (ValidationUtils.emailInvalid(request.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Недійсний email. Введіть коректну адресу електронної пошти."));
        } else if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse("Email вже використовується."));
        }

        if (ValidationUtils.passwordInvalid(request.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Недійсний пароль. Повинен містити 8–30 символів, 1 велику літеру, 1 цифру, 1 спецсимвол. Без кирилиці."));
        }

        if (request.isSubscribeToAds()) {
            subServiceSender.send(new SubSend(request.getEmail()));
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setProvider(AuthProvider.LOCAL);
        userRepository.save(user);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(jwtService.generateToken(user.getEmail(), user.getRole()));
        authResponse.setRole(user.getRole());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authResponse);
    }


    public ResponseEntity<?> loginUser(LoginRequest request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if (user.isEmpty() || user.get().getProvider() != AuthProvider.LOCAL) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неправильні облікові дані.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Невірний пароль.");
        }

        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(jwtService.generateToken(user.get().getEmail(), user.get().getRole()));
        authResponse.setRole(user.get().getRole());
        return ResponseEntity.ok(authResponse);
    }

    public ResponseEntity<?> forgotPassword(ForgotRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            subServiceSender.sendForgotPassword(request);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Повідомлення для пітвердження надіслано.");
    }

    public ResponseEntity<?> resetPassword(TokenRequest request) {
        if (!jwtService.isTokenValid(request.getToken())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("Недійсний або протермінований токен."));
        }

        if (ValidationUtils.passwordInvalid(request.getNewPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("Недійсний пароль. Повинен містити 8–30 символів, 1 велику літеру, 1 цифру, 1 спецсимвол. Без кирилиці."));
        }

        String email = jwtService.extractEmail(request.getToken());
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("Недійсний або протермінований токен."));
        }

        jwtService.blackListToken(request.getToken());
        User user = optionalUser.get();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.ok(new ApiResponse("Пароль успішно змінено."));
    }
    public ResponseEntity<?> loginWithGoogle(IdTokenRequest request) {
        GoogleIdToken.Payload payload = ValidationUtils.verifyToken(request.getIdToken(), clientId);
        if (payload == null) {
            return ResponseEntity.badRequest().body("Недійсний Google аккаунт");
        }

        String email = payload.getEmail();
        String firstName = (String) payload.get("given_name");
        String lastName = (String) payload.get("family_name");

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user = userOptional.orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFirstName(firstName != null ? firstName : "Unknown");
            newUser.setLastName(lastName != null ? lastName : "");
            newUser.setPassword(UUID.randomUUID().toString());
            newUser.setProvider(AuthProvider.GOOGLE);
            return userRepository.save(newUser);
        });

        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(jwtService.generateToken(user.getEmail(), user.getRole()));
        authResponse.setRole(user.getRole());
        return ResponseEntity.ok(authResponse);
    }
}


