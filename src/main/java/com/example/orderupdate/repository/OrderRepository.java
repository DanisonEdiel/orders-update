package com.example.orderupdate.repository;

import com.example.orderupdate.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByOrderId(String orderId);
    
    @Query("SELECT o FROM Order o WHERE o.orderId = :id OR CAST(o.id as string) = :id")
    Optional<Order> findByOrderIdOrId(@Param("id") String id);
}
