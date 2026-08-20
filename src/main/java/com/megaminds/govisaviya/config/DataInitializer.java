package com.megaminds.govisaviya.config;

import com.megaminds.govisaviya.entity.*;
import com.megaminds.govisaviya.repository.ProductRepository;
import com.megaminds.govisaviya.repository.RoleRepository;
import com.megaminds.govisaviya.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        // Drop the constraint that might be blocking discriminator values
        try {
            jdbcTemplate.execute("ALTER TABLE users DROP CONSTRAINT IF EXISTS users_user_type_check");
        } catch (Exception e) {
            log.warn("Could not drop constraint: {}", e.getMessage());
        }

        // 1. Ensure Roles
        Role adminRole = ensureRole("ADMIN");
        Role farmerRole = ensureRole("FARMER");
        Role buyerRole = ensureRole("BUYER");
        Role deliveryRole = ensureRole("DELIVERY");

        // 2. Ensure Mock Users
        ensureAdmin(adminRole);
        Farmer farmerKamal = ensureFarmer("farmer.kamal@govisaviya.com", "Kamal Perera", "+94712345678", "Nuwara Eliya", "5 Acres", "Carrots, Potatoes, Leeks", 12, "Highland Valley Farm, Nuwara Eliya", farmerRole);
        Farmer farmerSunil = ensureFarmer("farmer.sunil@govisaviya.com", "Sunil Bandara", "+94723456789", "Dambulla", "8 Acres", "Tomatoes, Chillies, Onions", 15, "Greenfields Estate, Dambulla", farmerRole);
        ensureBuyer("buyer.nimal@govisaviya.com", "Nimal Fernando", "+94773456789", "Kandy", "Ceylon Fresh Mart", "Retail", "Organic Vegetables, Fresh Fruits", buyerRole);
        ensureDeliveryPerson("delivery.saman@govisaviya.com", "Saman Kumara", "+94765432109", "Colombo", "WP CAB-4521", "Light Truck (2 Ton)", deliveryRole);

        // 3. Ensure Mock Products
        ensureMockProducts(farmerKamal, farmerSunil);

        log.info(">>> Mock data initialized successfully! <<<");
    }

    private Role ensureRole(String roleName) {
        return roleRepository.findByName(roleName).orElseGet(() -> {
            Role role = new Role();
            role.setName(roleName);
            return roleRepository.save(role);
        });
    }

    private Admin ensureAdmin(Role adminRole) {
        String email = "admin@govisaviya.com";
        return (Admin) userRepository.findByEmail(email).orElseGet(() -> {
            Admin admin = Admin.builder()
                    .email(email)
                    .fullName("System Administrator")
                    .password(passwordEncoder.encode("admin123"))
                    .phone("+94771234567")
                    .location("Colombo")
                    .department("Operations & Agronomy")
                    .adminLevel("Super Admin")
                    .enabled(true)
                    .roles(new HashSet<>(Collections.singletonList(adminRole)))
                    .build();
            return userRepository.save(admin);
        });
    }

    private Farmer ensureFarmer(String email, String name, String phone, String location, String farmSize, String cropTypes, int experience, String details, Role farmerRole) {
        return (Farmer) userRepository.findByEmail(email).orElseGet(() -> {
            Farmer farmer = Farmer.builder()
                    .email(email)
                    .fullName(name)
                    .password(passwordEncoder.encode("farmer123"))
                    .phone(phone)
                    .location(location)
                    .farmSize(farmSize)
                    .cropTypes(cropTypes)
                    .experience(experience)
                    .farmLocationDetails(details)
                    .enabled(true)
                    .roles(new HashSet<>(Collections.singletonList(farmerRole)))
                    .build();
            return userRepository.save(farmer);
        });
    }

    private Buyer ensureBuyer(String email, String name, String phone, String location, String businessName, String purpose, String cropTypes, Role buyerRole) {
        return (Buyer) userRepository.findByEmail(email).orElseGet(() -> {
            Buyer buyer = Buyer.builder()
                    .email(email)
                    .fullName(name)
                    .password(passwordEncoder.encode("buyer123"))
                    .phone(phone)
                    .location(location)
                    .businessName(businessName)
                    .buyingPurpose(purpose)
                    .preferredCropTypes(cropTypes)
                    .enabled(true)
                    .roles(new HashSet<>(Collections.singletonList(buyerRole)))
                    .build();
            return userRepository.save(buyer);
        });
    }

    private DeliveryPerson ensureDeliveryPerson(String email, String name, String phone, String location, String vehicleNumber, String vehicleType, Role deliveryRole) {
        return (DeliveryPerson) userRepository.findByEmail(email).orElseGet(() -> {
            DeliveryPerson delivery = DeliveryPerson.builder()
                    .email(email)
                    .fullName(name)
                    .password(passwordEncoder.encode("delivery123"))
                    .phone(phone)
                    .location(location)
                    .vehicleNumber(vehicleNumber)
                    .vehicleType(vehicleType)
                    .enabled(true)
                    .roles(new HashSet<>(Collections.singletonList(deliveryRole)))
                    .build();
            return userRepository.save(delivery);
        });
    }

    private void ensureMockProducts(Farmer farmer1, Farmer farmer2) {
        if (productRepository.count() == 0) {
            Product p1 = Product.builder()
                    .name("Fresh Nuwara Eliya Carrots")
                    .description("Freshly harvested grade-A sweet carrots from Nuwara Eliya organic farms.")
                    .category("Vegetables")
                    .pricePerUnit(new BigDecimal("420.00"))
                    .unit("kg")
                    .availableQuantity(new BigDecimal("250.00"))
                    .location("Nuwara Eliya")
                    .imageUrl("https://images.unsplash.com/photo-1598170845058-32b9d6a5da37?auto=format&fit=crop&w=600&q=80")
                    .farmer(farmer1)
                    .build();

            Product p2 = Product.builder()
                    .name("Highland Red Potatoes")
                    .description("Premium quality red skin potatoes, ideal for cooking and roasting.")
                    .category("Vegetables")
                    .pricePerUnit(new BigDecimal("380.00"))
                    .unit("kg")
                    .availableQuantity(new BigDecimal("500.00"))
                    .location("Nuwara Eliya")
                    .imageUrl("https://images.unsplash.com/photo-1518977676601-b53f82aba655?auto=format&fit=crop&w=600&q=80")
                    .farmer(farmer1)
                    .build();

            Product p3 = Product.builder()
                    .name("Organic Ripe Tomatoes")
                    .description("Naturally ripened juicy tomatoes directly from Dambulla farms.")
                    .category("Vegetables")
                    .pricePerUnit(new BigDecimal("290.00"))
                    .unit("kg")
                    .availableQuantity(new BigDecimal("300.00"))
                    .location("Dambulla")
                    .imageUrl("https://images.unsplash.com/photo-1592924357228-91a4daadcfea?auto=format&fit=crop&w=600&q=80")
                    .farmer(farmer2)
                    .build();

            Product p4 = Product.builder()
                    .name("Spicy Green Chillies")
                    .description("Fresh and sharp green chillies freshly picked for retail and commercial supply.")
                    .category("Vegetables")
                    .pricePerUnit(new BigDecimal("650.00"))
                    .unit("kg")
                    .availableQuantity(new BigDecimal("120.00"))
                    .location("Dambulla")
                    .imageUrl("https://images.unsplash.com/photo-1588252303782-cb80119abd6d?auto=format&fit=crop&w=600&q=80")
                    .farmer(farmer2)
                    .build();

            Product p5 = Product.builder()
                    .name("Traditional Keeri Samba Rice")
                    .description("Unpolished aromatic Keeri Samba rice, pesticide-free harvest.")
                    .category("Grains")
                    .pricePerUnit(new BigDecimal("260.00"))
                    .unit("kg")
                    .availableQuantity(new BigDecimal("1000.00"))
                    .location("Dambulla")
                    .imageUrl("https://images.unsplash.com/photo-1586201375761-83865001e31c?auto=format&fit=crop&w=600&q=80")
                    .farmer(farmer2)
                    .build();

            productRepository.save(p1);
            productRepository.save(p2);
            productRepository.save(p3);
            productRepository.save(p4);
            productRepository.save(p5);
            log.info("Initialized 5 sample products.");
        }
    }
}
