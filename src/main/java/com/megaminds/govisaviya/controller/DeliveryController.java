package com.megaminds.govisaviya.controller;

import com.megaminds.govisaviya.entity.Delivery;
import com.megaminds.govisaviya.entity.User;
import com.megaminds.govisaviya.service.DeliveryService;
import com.megaminds.govisaviya.util.RestURIs;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(RestURIs.DELIVERY)
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping(RestURIs.ASSIGN)
    public ResponseEntity<?> assignDelivery(
            @RequestParam Long orderId,
            @RequestParam Long deliveryPersonId) {
        try {
            return ResponseEntity.ok(deliveryService.assignDelivery(orderId, deliveryPersonId));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Assignment Failed: " + e.getMessage());
        }
    }

    @PatchMapping(RestURIs.UPDATE_STATUS + "/{id}")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body("Error: This action requires an active driver session.");
        }
        try {
            return ResponseEntity.ok(deliveryService.updateDeliveryStatus(id, status, auth.getName()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Fulfillment Error: " + e.getMessage());
        }
    }

    @GetMapping(RestURIs.MY_DELIVERIES)
    public ResponseEntity<List<Delivery>> getMyDeliveries(Authentication auth) {
        return ResponseEntity.ok(deliveryService.getMyDeliveries(auth.getName()));
    }

    @GetMapping(RestURIs.PERSONS)
    public ResponseEntity<List<User>> getAllDeliveryPersons() {
        return ResponseEntity.ok(deliveryService.getAllDeliveryPersons());
    }
}
