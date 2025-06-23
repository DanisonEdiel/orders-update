package com.example.orderupdate.controller;

import com.example.orderupdate.domain.Order;
import com.example.orderupdate.dto.UpdateOrderRequest;
import com.example.orderupdate.service.OrderUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "APIs for managing orders")
@Slf4j
public class OrderUpdateController {

    private final OrderUpdateService orderUpdateService;

    @Operation(
        summary = "Update an existing order", 
        description = "Updates an order if it's not completed or cancelled yet",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{orderId}")
    public ResponseEntity<?> updateOrder(
            @PathVariable String orderId,
            @Valid @RequestBody UpdateOrderRequest request) {
        
        log.info("Received order update request for order ID: {}", orderId);
        Order updatedOrder = orderUpdateService.updateOrder(orderId, request);
        
        log.info("Updated order with ID: {}", updatedOrder.getOrderId());
        
        return ResponseEntity.ok()
            .body(Map.of(
                "orderId", updatedOrder.getOrderId(),
                "status", updatedOrder.getStatus(),
                "message", "Order updated successfully.",
                "updatedAt", updatedOrder.getUpdatedAt()
            ));
    }
}
