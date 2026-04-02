package com.megaminds.govisaviya.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "farmers")
@DiscriminatorValue("FARMER")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Farmer extends User {
    
    @Column(nullable = true)
    private String farmSize;

    @Column(nullable = true)
    private String cropTypes;

    @Column(nullable = true)
    private Integer experience; // years

    @Column(columnDefinition = "TEXT")
    private String farmLocationDetails;
}
