package com.megaminds.govisaviya.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Role is required")
    private String roleName;

    // Farmer-specific Fields
    private String farmSize;
    private String cropTypes;
    private Integer experience;
    private String farmLocationDetails;

    // Buyer-specific Fields
    private String businessName;
    private String buyingPurpose;
    private String preferredCropTypes;
}
