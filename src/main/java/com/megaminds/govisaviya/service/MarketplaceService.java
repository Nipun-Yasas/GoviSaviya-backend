package com.megaminds.govisaviya.service;

import com.megaminds.govisaviya.entity.Order;
import com.megaminds.govisaviya.entity.OrderStatus;
import com.megaminds.govisaviya.entity.Product;

import java.util.List;

public interface MarketplaceService {

    // Product methods
    Product createProduct(Product product, String farmerEmail);
    List<Product> getAllProducts();
    List<Product> getFarmerProducts(String farmerEmail);
    List<Product> searchProductsByLocation(String location);
    List<Product> getProductsByCategory(String category);
    Product updateProduct(Long productId, Product updatedProduct, String farmerEmail);
    void deleteProduct(Long productId, String farmerEmail);

    // Order methods
    Order placeOrder(Order order, String buyerEmail);
    List<Order> getBuyerOrders(String buyerEmail);
    List<Order> getFarmerOrders(String farmerEmail);
    Order updateOrderStatus(Long orderId, OrderStatus status, String authenticatedUserEmail);
}
