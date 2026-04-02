package com.megaminds.govisaviya.service;

import com.megaminds.govisaviya.dto.request.LoginRequest;
import com.megaminds.govisaviya.dto.request.RegisterRequest;
import com.megaminds.govisaviya.dto.response.AuthResponse;
import com.megaminds.govisaviya.entity.Role;
import com.megaminds.govisaviya.entity.User;
import com.megaminds.govisaviya.repository.RoleRepository;
import com.megaminds.govisaviya.repository.UserRepository;
import com.megaminds.govisaviya.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);

        Role userRole = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role with given ID not found"));
        user.setRoles(Set.of(userRole));

        userRepository.save(user);

        String JWT = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(JWT)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String JWT = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(JWT)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                .build();
    }
}