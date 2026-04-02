package com.megaminds.govisaviya.service;

import com.megaminds.govisaviya.dto.request.LoginRequest;
import com.megaminds.govisaviya.dto.request.RegisterRequest;
import com.megaminds.govisaviya.dto.response.AuthResponse;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}