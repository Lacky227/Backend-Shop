package com.fullstackfamily.authservice.service;

import com.fullstackfamily.authservice.entity.User;
import com.fullstackfamily.authservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(User user) {

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Ім'я користувача вже зайняте");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Електронна пошта вже використовується");
        }


        String usernameRegex = "^[a-zA-Z0-9!@#$%^&*()_+\\-=<>?.:,]{5,15}$";
        if (!Pattern.matches(usernameRegex, user.getUsername())) {
            throw new IllegalArgumentException("Недійсне ім'я користувача. Повинно містити 5-15 символів, включаючи дозволені символи.");
        }


        String password = user.getPassword();
        if (password.length() > 50 || password.contains(" ") || password.matches(".*[а-яА-ЯїЇєЄіІґҐ].*")) {
            throw new IllegalArgumentException("Недійсний пароль. Має містити максимум 50 символів, без кирилиці та пробілів.");
        }

        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
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


