package com.megaminds.govisaviya.controller;

import com.megaminds.govisaviya.entity.Order;
import com.megaminds.govisaviya.entity.OrderStatus;
import com.megaminds.govisaviya.entity.Product;
import com.megaminds.govisaviya.service.MarketplaceService;
import com.megaminds.govisaviya.util.RestURIs;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(RestURIs.MARKETPLACE)
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class MarketplaceController {

    private final MarketplaceService marketplaceService;

    @PostMapping(RestURIs.PRODUCTS)
    public ResponseEntity<Product> createProduct(@RequestBody Product product, Authentication auth) {
        return ResponseEntity.ok(marketplaceService.createProduct(product, auth.getName()));
    }

    @GetMapping(RestURIs.PRODUCTS)
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(marketplaceService.getAllProducts());
    }

    @GetMapping(RestURIs.PRODUCTS + "/my")
    public ResponseEntity<List<Product>> getMyProducts(Authentication auth) {
        return ResponseEntity.ok(marketplaceService.getFarmerProducts(auth.getName()));
    }

    @PutMapping(RestURIs.PRODUCTS + "/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody Product product,
            Authentication auth) {
        return ResponseEntity.ok(marketplaceService.updateProduct(id, product, auth.getName()));
    }

    @DeleteMapping(RestURIs.PRODUCTS + "/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id, Authentication auth) {
        marketplaceService.deleteProduct(id, auth.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping(RestURIs.ORDERS)
    public ResponseEntity<Order> placeOrder(@RequestBody Order order, Authentication auth) {
        return ResponseEntity.ok(marketplaceService.placeOrder(order, auth.getName()));
    }

    @GetMapping(RestURIs.ORDERS + "/farmer")
    public ResponseEntity<List<Order>> getFarmerOrders(Authentication auth) {
        return ResponseEntity.ok(marketplaceService.getFarmerOrders(auth.getName()));
    }

    @GetMapping(RestURIs.ORDERS + "/buyer")
    public ResponseEntity<List<Order>> getBuyerOrders(Authentication auth) {
        return ResponseEntity.ok(marketplaceService.getBuyerOrders(auth.getName()));
    }

    @PatchMapping(RestURIs.ORDERS + "/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status,
            Authentication auth) {
        return ResponseEntity.ok(marketplaceService.updateOrderStatus(id, status, auth.getName()));
    }
}
