package com.megaminds.govisaviya.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "delivery_persons")
@DiscriminatorValue("DELIVERY")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class DeliveryPerson extends User {

    private String vehicleNumber;
    private String vehicleType;
}
