package com.threadvine.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemDTO {

    private UUID id;
    private UUID orderId;
    private UUID productId;
    @Positive(message = "Cannot be negative")
    private Integer quantity;
    @Positive(message = "Cannot be negative")
    private BigDecimal price;
}
