package com.megaminds.govisaviya.repository;

import com.megaminds.govisaviya.entity.Delivery;
import com.megaminds.govisaviya.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Optional<Delivery> findByOrder_Id(Long orderId);

    List<Delivery> findByDeliveryPerson(User deliveryPerson);
}
