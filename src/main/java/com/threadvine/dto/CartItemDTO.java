package com.threadvine.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemDTO {

    private Long id;
    private Long cartId;
    private Long productId;
    @Positive(message = "Cannot be negative")
    private Integer quantity;

}
