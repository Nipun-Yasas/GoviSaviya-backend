package com.megaminds.govisaviya.service;

import com.megaminds.govisaviya.entity.Delivery;
import com.megaminds.govisaviya.entity.User;
import java.util.List;

public interface DeliveryService {

    Delivery assignDelivery(Long orderId, Long deliveryPersonId);

    Delivery updateDeliveryStatus(Long deliveryId, String status, String deliveryPersonEmail);

    List<Delivery> getMyDeliveries(String deliveryPersonEmail);

    List<User> getAllDeliveryPersons();
}
