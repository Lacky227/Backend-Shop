package com.fullstackfamily.authservice.service;

import com.fullstackfamily.authservice.dto.RegisterRequest;
import com.fullstackfamily.authservice.entity.User;
import com.fullstackfamily.authservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<String> registerUser(RegisterRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Ім'я користувача вже зайняте");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Почта користувача вже зайнята");
        }


        String usernameRegex = "^[a-zA-Z0-9!@#$%^&*()_+\\-=<>?.:,]{5,15}$";
        if (!Pattern.matches(usernameRegex, request.getUsername())) {
            return ResponseEntity.badRequest().body("Недійсне ім'я користувача. Повинно містити 5-15 символів, включаючи дозволені символи.");
        }


        String password = request.getPassword();
        if (password.length() > 50 || password.contains(" ") || password.matches(".*[а-яА-ЯїЇєЄіІґҐ].*")) {
            return ResponseEntity.badRequest().body("Недійсний пароль. Має містити максимум 50 символів, без кирилиці та пробілів.");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return ResponseEntity.ok("Зарегистрировано");
    }


    public User loginUser(String username, String password) {

        String usernameRegex = "^[a-zA-Z0-9!@#$%^&*()_+\\-=<>?.:,]{5,15}$";
        if (!Pattern.matches(usernameRegex, username)) {
            throw new IllegalArgumentException("Недійсний логін. Повинен містити 5-15 символів, включаючи латинські літери, цифри та спеціальні символи, без пробілів і кирилиці.");
        }


        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Користувача з таким логіном не знайдено."));


        if (password.length() > 50 || password.contains(" ") || password.matches(".*[а-яА-ЯїЇєЄіІґҐ].*")) {
            throw new IllegalArgumentException("Недійсний пароль. Має містити максимум 50 символів, без кирилиці та пробілів.");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Неправильний пароль.");
        }

        return user;
    }

}


