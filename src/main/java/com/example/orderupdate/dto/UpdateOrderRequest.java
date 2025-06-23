package com.example.orderupdate.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class UpdateOrderRequest {
    @NotEmpty(message = "Order items cannot be empty")
    private List<@Valid OrderItemDto> items;
}
