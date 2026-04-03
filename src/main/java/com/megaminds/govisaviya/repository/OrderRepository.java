package com.megaminds.govisaviya.repository;

import com.megaminds.govisaviya.entity.Order;
import com.megaminds.govisaviya.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByBuyer(User buyer);

    @Query("SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.product.farmer = :farmer")
    List<Order> findByFarmer(@Param("farmer") User farmer);
}
