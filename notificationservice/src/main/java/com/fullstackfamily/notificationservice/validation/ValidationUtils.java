package com.fullstackfamily.notificationservice.validation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationUtils {
    private final String EMAIL_PATTERN = "^(?=.{10,50}$)[A-Za-z0-9](?!.*[._+-]{2})[A-Za-z0-9+_.-]*[A-Za-z0-9]@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.(com|org|ua|net)$";

    public boolean validateEmail(String email) {
        if (email == null || email.isEmpty()) return true;
        return !email.matches(EMAIL_PATTERN);
    }
}
