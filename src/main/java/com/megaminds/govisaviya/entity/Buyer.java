package com.megaminds.govisaviya.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "buyers")
@DiscriminatorValue("BUYER")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Buyer extends User {

    private String businessName;

    private String buyingPurpose; // Retail / Restaurant / Export

    @Column(nullable = true)
    private String preferredCropTypes;
}
