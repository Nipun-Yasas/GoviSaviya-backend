package com.megaminds.govisaviya.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "admins")
@DiscriminatorValue("ADMIN")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Admin extends User {
    
    /** 
     * Administrative rank or department 
     * (e.g., 'Senior Officer', 'Regional Manager')
     */
    @Column(nullable = true)
    private String department;

    @Column(nullable = true)
    private String adminLevel;
}
