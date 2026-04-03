package com.megaminds.govisaviya.service.impl;

import com.megaminds.govisaviya.dto.response.UserResponse;
import com.megaminds.govisaviya.entity.User;
import com.megaminds.govisaviya.repository.UserRepository;
import com.megaminds.govisaviya.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .noneMatch(role -> role.getName().equalsIgnoreCase("ADMIN")))
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void disableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Override
    public void enableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setEnabled(true);
        userRepository.save(user);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .location(user.getLocation())
                .enabled(user.isEnabled())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName())
                        .collect(Collectors.toSet()))
                .build();
    }
}
