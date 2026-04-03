package com.megaminds.govisaviya.service.impl;

import com.megaminds.govisaviya.entity.*;
import com.megaminds.govisaviya.repository.DeliveryRepository;
import com.megaminds.govisaviya.repository.OrderRepository;
import com.megaminds.govisaviya.repository.UserRepository;
import com.megaminds.govisaviya.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Delivery assignDelivery(Long orderId, Long deliveryPersonId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        User deliveryPerson = userRepository.findById(deliveryPersonId)
                .orElseThrow(() -> new RuntimeException("Delivery person not found"));

        // Sync order status
        order.setStatus(OrderStatus.ASSIGNED);
        orderRepository.save(order);

        // Remove old if exists
        deliveryRepository.findByOrderId(orderId).ifPresent(deliveryRepository::delete);

        Delivery delivery = Delivery.builder()
                .order(order)
                .deliveryPerson(deliveryPerson)
                .status(OrderStatus.ASSIGNED)
                .assignedAt(LocalDateTime.now())
                .build();

        return deliveryRepository.save(delivery);
    }



    @Override
    @Transactional
    public Delivery updateDeliveryStatus(Long deliveryId, String status, String deliveryPersonEmail) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery record not found"));

        if (delivery.getDeliveryPerson() == null || !delivery.getDeliveryPerson().getEmail().equalsIgnoreCase(deliveryPersonEmail)) {
            throw new RuntimeException("Unauthorized: This delivery is assigned to another driver.");
        }

        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            delivery.setStatus(orderStatus);
            
            if (orderStatus == OrderStatus.PICKED_UP) {
                delivery.setPickedUpAt(LocalDateTime.now());
            } else if (orderStatus == OrderStatus.DELIVERED) {
                delivery.setDeliveredAt(LocalDateTime.now());
            }

            // Sync with main order
            Order order = delivery.getOrder();
            if (order != null) {
                order.setStatus(orderStatus);
                orderRepository.save(order);
            }

            return deliveryRepository.save(delivery);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status provided: " + status);
        }
    }



    @Override
    public List<Delivery> getMyDeliveries(String deliveryPersonEmail) {
        User deliveryPerson = userRepository.findByEmail(deliveryPersonEmail)
                .orElseThrow(() -> new RuntimeException("Delivery person not found"));
        return deliveryRepository.findByDeliveryPerson(deliveryPerson);
    }

    @Override
    public List<User> getAllDeliveryPersons() {
        return userRepository.findByRoles_Name("DELIVERY");
    }
}

