package com.fullstackfamily.authservice.controller;


import com.fullstackfamily.authservice.dto.LoginRequest;
import com.fullstackfamily.authservice.dto.RegisterRequest;
import com.fullstackfamily.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    public final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        return userService.registerUser(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return userService.loginUser(request);
    }

    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody String idToken) {
        return userService.loginWithGoogle(idToken);
    }

}