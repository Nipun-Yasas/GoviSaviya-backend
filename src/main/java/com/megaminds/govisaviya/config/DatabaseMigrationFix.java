package com.megaminds.govisaviya.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Emergency migration script to fix Check Constraint issues in PostgreSQL 
 * that happen when new values (like PICKED_UP) are added to an existing Enum.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DatabaseMigrationFix {

    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void fixEnumConstraints() {
        try {
            log.info("Checking database constraints for OrderStatus updates...");
            
            // Drop traditional check constraints that Hibernate creates for @Enumerated(EnumType.STRING)
            // We use 'IF EXISTS' to manage safety across different environments
            jdbcTemplate.execute("ALTER TABLE IF EXISTS orders DROP CONSTRAINT IF EXISTS orders_status_check");
            jdbcTemplate.execute("ALTER TABLE IF EXISTS deliveries DROP CONSTRAINT IF EXISTS deliveries_status_check");
            
            log.info("Database constraints successfully relaxed for order status transitions.");
        } catch (Exception e) {
            log.warn("Non-critical migration warning: Could not drop constraints (they may have already been removed). Details: {}", e.getMessage());
        }
    }
}
