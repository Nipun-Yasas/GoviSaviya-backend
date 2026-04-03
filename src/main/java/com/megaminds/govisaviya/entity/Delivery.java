package com.megaminds.govisaviya.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "delivery_users")
@DiscriminatorValue("DELIVERY") // This fixes the error
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Delivery extends User {
    // Add delivery-specific fields here (e.g., vehicleType, licenseNumber)
    private String vehicleType;
}
