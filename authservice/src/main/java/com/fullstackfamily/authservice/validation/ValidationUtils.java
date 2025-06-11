package com.fullstackfamily.authservice.validation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationUtils {
    private final String EMAIL_PATTERN =
            "^(?=.{10,50}$)[A-Za-z0-9](?!.*[._+-]{2})[A-Za-z0-9+_.-]*[A-Za-z0-9]@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.(com|org|ua|net)$";
    private final String PASSWORD_PATTERN =
            "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+=\\-\\[\\]{};':\"\\\\|,.<>/?]).{8,30}$";

    public boolean emailInvalid(String email) {
        if (email == null || email.isEmpty()) return true;
        return !email.matches(EMAIL_PATTERN);
    }

    public boolean passwordInvalid(String password) {
        if (password == null || password.isEmpty()) return true;
        if (password.length() < 8 || password.length() > 30) return true;
        if (password.matches(".*[а-яА-ЯїЇєЄіІґҐ].*")) return true;
        return !password.matches(PASSWORD_PATTERN);
    }
    public boolean usernameInvalid(String username) {
        return username == null || username.isEmpty() ||
                !username.matches("^[a-zA-Z0-9!@#$%^&*()_+\\-=<>?.:,]{5,15}$");
    }
}
