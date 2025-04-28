package com.threadvine.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemDTO {

    private UUID id;
    private UUID productId;
    @Positive(message = "Cannot be negative")
    private Integer quantity;

}
