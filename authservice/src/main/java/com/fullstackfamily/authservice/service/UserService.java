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

        if (ValidationUtils.usernameInvalid(request.getUsername())) {
            return ResponseEntity.badRequest().body("Недійсне ім'я користувача. Повинно містити 5-15 символів, включаючи дозволені символи.");
        } else if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Ім'я користувача вже зайняте");
        }

        if (ValidationUtils.emailInvalid(request.getEmail())) {
            return ResponseEntity.badRequest().body("Недійсний email. Введіть коректну адресу електронної пошти.");
        } else if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Почта користувача вже зайнята");
        }

        if (ValidationUtils.passwordInvalid(request.getPassword())) {
            return ResponseEntity.badRequest().body("Недійсний пароль. Має бути НЕ пустим, містити максимум 50 символів, без кирилиці та пробілів.");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("Зарегистрировано");
    }


    public ResponseEntity<?> loginUser(LoginRequest request) {

        if (ValidationUtils.emailInvalid(request.getEmail())) {
            return ResponseEntity.badRequest().body("Недійсний email. Введіть коректну адресу електронної пошти.");
        }

        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("НЕ знайдено користувача. Будь ласка перевірте email.");
        }

        if (ValidationUtils.passwordInvalid(request.getPassword())) {
            return ResponseEntity.badRequest().body("Недійсний пароль. Має бути НЕ пустим, містити максимум 50 символів, без кирилиці та пробілів.");
        } else if (!passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
            return ResponseEntity.badRequest().body("Невірний пароль.");
        }
        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(jwtService.generateToken(user.get().getEmail(), user.get().getRole()));
        authResponse.setRole(user.get().getRole());
        return ResponseEntity.ok(authResponse);
    }
}


