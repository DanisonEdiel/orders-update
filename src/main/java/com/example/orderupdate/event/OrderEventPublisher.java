package com.example.orderupdate.event;

import com.example.orderupdate.domain.Order;
import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private final SnsTemplate snsTemplate;

    @Value("${cloud.aws.sns.topic.order-updated}")
    private String orderUpdatedTopicArn;

    public void publishOrderUpdatedEvent(Order order) {
        OrderUpdatedEvent event = new OrderUpdatedEvent(
                order.getOrderId(),
                order.getUserId(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getUpdatedAt(),
                order.getItems().stream()
                        .map(item -> new OrderUpdatedEvent.OrderItemEvent(
                                item.getProductName(),
                                item.getQuantity(),
                                item.getUnitPrice()
                        ))
                        .collect(Collectors.toList())
        );

        log.info("Publishing order updated event for order ID: {}", order.getOrderId());
        snsTemplate.sendNotification(orderUpdatedTopicArn, event, "OrderUpdated");
    }

    public record OrderUpdatedEvent(
            String orderId,
            String userId,
            String status,
            BigDecimal totalAmount,
            LocalDateTime updatedAt,
            List<OrderItemEvent> items
    ) {
        public record OrderItemEvent(
                String productName,
                Integer quantity,
                BigDecimal unitPrice
        ) {}
    }
}
