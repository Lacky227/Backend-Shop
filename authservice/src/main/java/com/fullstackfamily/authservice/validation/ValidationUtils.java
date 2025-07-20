package com.fullstackfamily.authservice.validation;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.experimental.UtilityClass;

import java.util.Collections;

@UtilityClass
public class ValidationUtils {
    private final String EMAIL_PATTERN =
            "^(?=.{10,50}$)[A-Za-z0-9](?!.*[._+-]{2})[A-Za-z0-9+_.-]*[A-Za-z0-9]@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.(com|org|ua|net)$";
    private final String PASSWORD_PATTERN =
            "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+=\\-\\[\\]{};':\"\\\\|,.<>/?]).{8,30}$";
    private final String NAME_PATTERN =
            "^[A-Za-zА-Яа-яЇїІіЄєҐґ\\-\\s]{1,15}$";

    public boolean emailInvalid(String email) {
        if (email == null || email.isEmpty()) return true;
        return !email.matches(EMAIL_PATTERN);
    }

    public boolean passwordInvalid(String password) {
        if (password == null || password.isEmpty()) return true;
        if (password.matches(".*[а-яА-ЯїЇєЄіІґҐ].*")) return true;
        return !password.matches(PASSWORD_PATTERN);
    }

    public boolean firstNameInvalid(String name) {
        return name == null || name.isEmpty() || !name.matches(NAME_PATTERN);
    }
    public boolean lastNameInvalid(String name) {
        return name != null && !name.isEmpty() && !name.matches(NAME_PATTERN);
    }
}
