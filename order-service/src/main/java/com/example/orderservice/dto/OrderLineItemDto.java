package com.example.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderLineItemDto {
    @NotBlank(message = "Product ID is required")
    private String productId;
    
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
}