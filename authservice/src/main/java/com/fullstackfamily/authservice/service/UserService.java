package com.fullstackfamily.authservice.service;

import com.fullstackfamily.authservice.dto.*;
import com.fullstackfamily.authservice.entity.User;
import com.fullstackfamily.authservice.repository.UserRepository;
import com.fullstackfamily.authservice.validation.ValidationUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SubServiceSender subServiceSender;

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
        userRepository.save(user);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(jwtService.generateToken(user.getEmail(), user.getRole()));
        authResponse.setRole(user.getRole());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authResponse);
    }


    public ResponseEntity<?> loginUser(LoginRequest request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Не знайдено користувача. Будь ласка, перевірте email.");
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("Користувача не знайдено."));
        }

        User user = optionalUser.get();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(new ApiResponse("Пароль успішно змінено."));
    }
}


