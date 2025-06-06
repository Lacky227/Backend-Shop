package com.fullstackfamily.authservice.validation;

public class Valid {

    public static boolean emailInvalid(String email) {
        return email == null || email.isEmpty() ||
                !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public static boolean passwordInvalid(String password) {
        return password == null || password.isEmpty() ||
                password.length() > 50 ||
                password.matches(".*[а-яА-ЯїЇєЄіІґҐ].*");
    }
    public static boolean usernameInvalid(String username) {
        return username == null || username.isEmpty() ||
                !username.matches("^[a-zA-Z0-9!@#$%^&*()_+\\-=<>?.:,]{5,15}$");
    }
}
