package com.megaminds.govisaviya.controller;

import com.megaminds.govisaviya.service.AuthService;
import com.megaminds.govisaviya.util.RestURIs;
import lombok.RequiredArgsConstructor;

import com.megaminds.govisaviya.dto.request.LoginRequest;
import com.megaminds.govisaviya.dto.request.RegisterRequest;
import com.megaminds.govisaviya.dto.response.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping(RestURIs.AUTH)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(RestURIs.REGISTER)
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping(RestURIs.LOGIN)
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping(RestURIs.LOGOUT)
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Successfully logged out");
    }

}
