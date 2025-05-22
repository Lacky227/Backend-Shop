package com.fullstackfamily.authservice.service;

import com.fullstackfamily.authservice.entity.User;
import com.fullstackfamily.authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

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


        return userRepository.save(user);
    }
}
