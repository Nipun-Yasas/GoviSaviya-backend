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
                .orElseThrow(() -> new RuntimeException("Order #" + orderId + " not found in system."));
        
        if (!order.isDeliveryRequired()) {
            throw new RuntimeException("Operational Error: Order #" + orderId + " was marked for farm pickup, not delivery.");
        }

        User deliveryPerson = userRepository.findById(deliveryPersonId)
                .orElseThrow(() -> new RuntimeException("Delivery Person #" + deliveryPersonId + " not found."));

        // Validate role (optional check for added safety)
        boolean isDriver = deliveryPerson.getRoles().stream()
                .anyMatch(r -> r.getName().equalsIgnoreCase("DELIVERY"));
        
        if (!isDriver) {
            throw new RuntimeException("Actor Error: Selected user #" + deliveryPersonId + " is not registered as a Delivery Person.");
        }

        // Sync order status
        order.setStatus(OrderStatus.ASSIGNED);
        orderRepository.save(order);

        // Update existing delivery record or create new one to avoid unique constraint issues
        Delivery delivery = deliveryRepository.findByOrder_Id(orderId)
                .orElse(Delivery.builder().order(order).build());

        delivery.setDeliveryPerson(deliveryPerson);
        delivery.setStatus(OrderStatus.ASSIGNED);
        delivery.setAssignedAt(LocalDateTime.now());
        delivery.setPickedUpAt(null); // Reset if re-assigned
        delivery.setDeliveredAt(null); // Reset if re-assigned

        return deliveryRepository.save(delivery);
    }



    @Override
    @Transactional
    public Delivery updateDeliveryStatus(Long deliveryId, String status, String deliveryPersonEmail) {
        if (deliveryPersonEmail == null) {
            throw new RuntimeException("Unauthorized: Authentication required.");
        }

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery record not found with id: " + deliveryId));

        if (delivery.getDeliveryPerson() == null || !delivery.getDeliveryPerson().getEmail().equalsIgnoreCase(deliveryPersonEmail)) {
            throw new RuntimeException("Unauthorized: This delivery is assigned to another driver or is not assigned.");
        }

        try {
            OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase().trim());
            delivery.setStatus(newStatus);
            
            if (newStatus == OrderStatus.PICKED_UP) {
                delivery.setPickedUpAt(LocalDateTime.now());
            } else if (newStatus == OrderStatus.DELIVERED) {
                delivery.setDeliveredAt(LocalDateTime.now());
            }

            // Sync with main order
            Order order = delivery.getOrder();
            if (order != null) {
                order.setStatus(newStatus);
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

