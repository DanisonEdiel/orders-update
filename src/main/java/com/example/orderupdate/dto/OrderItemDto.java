package com.example.orderupdate.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDto {
    @NotBlank(message = "Product name is required")
    private String productName;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    @NotNull(message = "Unit price is required")
    @Min(value = 0, message = "Unit price cannot be negative")
    private BigDecimal unitPrice;
}
