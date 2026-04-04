package com.megaminds.govisaviya.service.impl;

import com.megaminds.govisaviya.entity.*;
import com.megaminds.govisaviya.repository.DeliveryRepository;
import com.megaminds.govisaviya.repository.OrderRepository;
import com.megaminds.govisaviya.repository.ProductRepository;
import com.megaminds.govisaviya.repository.UserRepository;
import com.megaminds.govisaviya.service.MarketplaceService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketplaceServiceImpl implements MarketplaceService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final DeliveryRepository deliveryRepository;

    @Override
    public Product createProduct(Product product, String farmerEmail) {
        User farmer = userRepository.findByEmail(farmerEmail)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));
        product.setFarmer(farmer);
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getFarmerProducts(String farmerEmail) {
        User farmer = userRepository.findByEmail(farmerEmail)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));
        return productRepository.findByFarmer(farmer);
    }

    @Override
    public List<Product> searchProductsByLocation(String location) {
        return productRepository.findByLocationContainingIgnoreCase(location);
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    @Override
    @Transactional
    public Order placeOrder(Order order, String buyerEmail) {
        User buyer = userRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
        
        order.setBuyer(buyer);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderedAt(java.time.LocalDateTime.now());

        
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                Product product = productRepository.findById(item.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProduct().getId()));
                
                if (product.getAvailableQuantity().compareTo(item.getQuantity()) < 0) {
                    throw new RuntimeException("Insufficient stock for: " + product.getName());
                }
                
                // Reduce inventory
                product.setAvailableQuantity(product.getAvailableQuantity().subtract(item.getQuantity()));
                productRepository.save(product);
                
                // Linkage and Price Persistence
                item.setOrder(order);
                item.setProduct(product);
                item.setPriceAtOrder(product.getPricePerUnit()); // Ensure fresh price from DB
            }
        }
        
        return orderRepository.save(order);
    }


    @Override
    public List<Order> getBuyerOrders(String buyerEmail) {
        User buyer = userRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
        return orderRepository.findByBuyer(buyer);
    }

    @Override
    public List<Order> getFarmerOrders(String farmerEmail) {
        return orderRepository.findByFarmerEmail(farmerEmail);
    }


    @Override
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus status, String authenticatedUserEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        
        order.setStatus(status);
        
        // Sync any associated delivery records
        deliveryRepository.findByOrder_Id(orderId).ifPresent(delivery -> {
            delivery.setStatus(status);
            deliveryRepository.save(delivery);
        });

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Product updateProduct(Long productId, Product updatedProduct, String farmerEmail) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!existingProduct.getFarmer().getEmail().equals(farmerEmail)) {
            throw new RuntimeException("Unauthorized to update this product");
        }

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setCategory(updatedProduct.getCategory());
        existingProduct.setPricePerUnit(updatedProduct.getPricePerUnit());
        existingProduct.setUnit(updatedProduct.getUnit());
        existingProduct.setAvailableQuantity(updatedProduct.getAvailableQuantity());
        existingProduct.setLocation(updatedProduct.getLocation());
        
        return productRepository.save(existingProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId, String farmerEmail) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!existingProduct.getFarmer().getEmail().equals(farmerEmail)) {
            throw new RuntimeException("Unauthorized to delete this product");
        }

        productRepository.delete(existingProduct);
    }
}
