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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderUpdateService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;

    @Transactional
    public Order updateOrder(String orderId, UpdateOrderRequest request) {
        log.info("Updating order with ID: {}", orderId);
        
        // Find the order
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));
        
        // Check if the order belongs to the authenticated user
        String currentUserId = getCurrentUserId();
        if (!order.getUserId().equals(currentUserId)) {
            throw new AccessDeniedException("You don't have permission to update this order");
        }
        
        // Check if the order can be updated
        if (!order.isUpdatable()) {
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
        
        log.info("Order updated successfully with ID: {}", orderId);
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
