package com.fullstackfamily.authservice.service;

import com.fullstackfamily.authservice.dto.AuthResponse;
import com.fullstackfamily.authservice.dto.LoginRequest;
import com.fullstackfamily.authservice.dto.RegisterRequest;
import com.fullstackfamily.authservice.entity.User;
import com.fullstackfamily.authservice.repository.UserRepository;
import com.fullstackfamily.authservice.validation.ValidationUtils;
import lombok.AllArgsConstructor;
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

    public ResponseEntity<String> registerUser(RegisterRequest request) {
        if (ValidationUtils.firstNameInvalid(request.getFirstName())){
            return ResponseEntity.badRequest().body("Ім’я є обов’язковим. Має містити від 1 до 15 символів.");
        } else if (ValidationUtils.firstNameInvalid(request.getLastName())) {
            return ResponseEntity.badRequest().body("Прізвище не коректне. Має містити від 1 до 15 символів.");
        }
        if (ValidationUtils.emailInvalid(request.getEmail())) {
            return ResponseEntity.badRequest().body("Недійсний email. Введіть коректну адресу електронної пошти.");
        } else if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email вже використовується.");
        }

        if (ValidationUtils.passwordInvalid(request.getPassword())) {
            return ResponseEntity.badRequest().body("Недійсний пароль. Повинен містити 8–30 символів, 1 велику літеру, 1 цифру, 1 спецсимвол. Без кирилиці.");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Зареєстровано.");
    }


    public ResponseEntity<?> loginUser(LoginRequest request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("НЕ знайдено користувача. Будь ласка перевірте email.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
            return ResponseEntity.badRequest().body("Невірний пароль.");
        }
        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(jwtService.generateToken(user.get().getEmail(), user.get().getRole()));
        authResponse.setRole(user.get().getRole());
        return ResponseEntity.ok(authResponse);
    }
}


