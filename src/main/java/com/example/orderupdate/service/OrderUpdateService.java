package com.example.orderupdate.service;

import com.example.orderupdate.domain.Order;
import com.example.orderupdate.domain.OrderItem;
import com.example.orderupdate.dto.OrderItemDto;
import com.example.orderupdate.dto.UpdateOrderRequest;
import com.example.orderupdate.event.OrderEventPublisher;
import com.example.orderupdate.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderUpdateService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;

    @Transactional
    public Order updateOrder(String orderId, UpdateOrderRequest request) {
        log.info("Updating order with ID: {}", orderId);
        
        // Find the order using the new method that checks both id and orderId
        log.debug("Searching for order with ID: {} in the database (checking both id and orderId)", orderId);
        Optional<Order> orderOpt = orderRepository.findByOrderIdOrId(orderId);
        
        if (orderOpt.isEmpty()) {
            log.error("Order not found with ID: {} (checked both id and orderId)", orderId);
            // Intentar buscar por ID primario en caso de confusión de IDs
            try {
                UUID uuid = UUID.fromString(orderId);
                Optional<Order> orderByPrimaryId = orderRepository.findById(uuid);
                if (orderByPrimaryId.isPresent()) {
                    log.info("Found order by primary ID: {}", orderId);
                    return updateOrderInternal(orderByPrimaryId.get(), request);
                }
            } catch (IllegalArgumentException e) {
                log.debug("The provided orderId is not a valid UUID: {}", orderId);
            }
            
            throw new EntityNotFoundException("Order not found with ID: " + orderId);
        }
        
        Order order = orderOpt.get();
        log.info("Found order: ID={}, OrderID={}, UserID={}, Status={}", 
                order.getId(), order.getOrderId(), order.getUserId(), order.getStatus());
        
        return updateOrderInternal(order, request);
    }
    
    private Order updateOrderInternal(Order order, UpdateOrderRequest request) {
        // Check if the order belongs to the authenticated user
        String currentUserId = getCurrentUserId();
        log.debug("Current authenticated user ID: {}", currentUserId);
        log.debug("Order's user ID: {}", order.getUserId());
        
        if (!order.getUserId().equals(currentUserId)) {
            log.error("Access denied: User {} attempted to update order belonging to user {}", 
                    currentUserId, order.getUserId());
            throw new AccessDeniedException("You don't have permission to update this order");
        }
        
        // Check if the order can be updated
        if (!order.isUpdatable()) {
            log.error("Order cannot be updated because it is already completed or cancelled: {}", order.getStatus());
            throw new IllegalStateException("Order cannot be updated because it is already completed or cancelled");
        }
        
        // Update the order items
        updateOrderItems(order, request);
        
        // Recalculate the total price
        order.calculateTotalPrice();
        
        // Mark as updated
        order.markAsUpdated();
        
        // Save the updated order
        Order updatedOrder = orderRepository.save(order);
        
        // Publish the order updated event
        orderEventPublisher.publishOrderUpdatedEvent(updatedOrder);
        
        log.info("Order updated successfully with ID: {}", order.getOrderId());
        return updatedOrder;
    }
    
    private void updateOrderItems(Order order, UpdateOrderRequest request) {
        // Clear existing items
        order.clearItems();
        
        // Add new items from the request
        request.getItems().forEach(itemDto -> {
            OrderItem item = new OrderItem()
                    .setProductName(itemDto.getProductName())
                    .setQuantity(itemDto.getQuantity())
                    .setUnitPrice(itemDto.getUnitPrice());
            
            order.addItem(item);
        });
    }
    
    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
