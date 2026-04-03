package com.megaminds.govisaviya.service;

import com.megaminds.govisaviya.dto.response.UserResponse;
import java.util.List;

public interface UserService {
    /**
     * Get all users in the system.
     * @return List of user details
     */
    List<UserResponse> getAllUsers();

    /**
     * Set a user's enabled status to false.
     * @param id The ID of the user to disable
     */
    void disableUser(Long id);

    /**
     * Set a user's enabled status to true.
     * @param id The ID of the user to enable
     */
    void enableUser(Long id);
}
