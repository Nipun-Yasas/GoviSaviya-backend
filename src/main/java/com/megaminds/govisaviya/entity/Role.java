package com.megaminds.govisaviya.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "roles")
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    public enum RoleName {
        ADMIN,
        OFFICER,
        CUSTOMER
    }
}
