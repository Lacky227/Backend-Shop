package com.fullstackfamily.authservice.service;

import com.fullstackfamily.authservice.dto.ApiResponse;
import com.fullstackfamily.authservice.dto.AuthResponse;
import com.fullstackfamily.authservice.dto.LoginRequest;
import com.fullstackfamily.authservice.dto.RegisterRequest;
import com.fullstackfamily.authservice.entity.User;
import com.fullstackfamily.authservice.repository.UserRepository;
import com.fullstackfamily.authservice.validation.ValidationUtils;
import lombok.AllArgsConstructor;
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

    public ResponseEntity<ApiResponse> registerUser(RegisterRequest request) {
        if (ValidationUtils.firstNameInvalid(request.getFirstName())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Ім’я є обов’язковим. Має містити від 1 до 15 символів."));
        } else if (ValidationUtils.firstNameInvalid(request.getLastName())) {
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

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("Зареєстровано."));
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
}


