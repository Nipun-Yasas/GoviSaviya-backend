package com.megaminds.govisaviya.controller;

import com.megaminds.govisaviya.dto.response.UserResponse;
import com.megaminds.govisaviya.service.UserService;
import com.megaminds.govisaviya.util.RestURIs;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for user account management by administrators.
 */
@RestController
@RequestMapping(RestURIs.USERS)
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    /**
     * Admin: Retrieve all users in the system.
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Admin: Disable a specific user account (set enabled = false).
     */
    @PatchMapping(RestURIs.USER_DISABLE)
    public ResponseEntity<String> disableUser(@PathVariable Long id) {
        userService.disableUser(id);
        return ResponseEntity.ok("User account disabled successfully.");
    }

    /**
     * Admin: Enable a specific user account (set enabled = true).
     */
    @PatchMapping(RestURIs.USER_ENABLE)
    public ResponseEntity<String> enableUser(@PathVariable Long id) {
        userService.enableUser(id);
        return ResponseEntity.ok("User account enabled successfully.");
    }
}
