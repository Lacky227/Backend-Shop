package com.fullstackfamily.notificationservice.validation;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class ValidationUtils {
    private final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.com$";

    public boolean validateEmail(String email) {
        if (email == null || email.isEmpty()) return true;
        if (email.length() < 14 || email.length() > 82) return true;
        return !email.matches(EMAIL_PATTERN);
    }
}
