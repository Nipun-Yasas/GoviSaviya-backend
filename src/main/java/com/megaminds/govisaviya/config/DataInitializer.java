package com.megaminds.govisaviya.config;

import com.megaminds.govisaviya.entity.Role;
import com.megaminds.govisaviya.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        ensureRole("ADMIN");
        ensureRole("FARMER");
        ensureRole("BUYER");
    }

    private void ensureRole(String roleName) {
        if (!roleRepository.existsByName(roleName)) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
        }
    }
}
