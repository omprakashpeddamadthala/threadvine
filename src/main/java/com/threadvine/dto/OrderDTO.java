package com.threadvine.dto;

import com.threadvine.model.Order;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {
    private UUID id;
    private UUID userId;
    @NotBlank(message = "Address  is required")
    private String address;
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    private Order.OrderStatus status;
    private LocalDateTime createdAt;
    private List<OrderItemDTO> items;
}
