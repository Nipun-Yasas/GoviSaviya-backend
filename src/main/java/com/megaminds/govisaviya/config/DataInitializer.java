package com.megaminds.govisaviya.config;

import com.megaminds.govisaviya.entity.Role;
import com.megaminds.govisaviya.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        // Drop the constraint that might be blocking the 'DELIVERY' discriminator value
        try {
            jdbcTemplate.execute("ALTER TABLE users DROP CONSTRAINT IF EXISTS users_user_type_check");
        } catch (Exception e) {
            System.err.println("Could not drop constraint: " + e.getMessage());
        }
        ensureRole("ADMIN");
        ensureRole("FARMER");
        ensureRole("BUYER");
        ensureRole("DELIVERY");
    }


    private void ensureRole(String roleName) {
        if (!roleRepository.existsByName(roleName)) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
        }
    }
}
