package com.threadvine.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItem  extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "order_id",nullable = false)
    private Order order;
    @ManyToOne
    @JoinColumn(name = "product_id",nullable = false)
    private Product product;
    private Integer quantity;
    private BigDecimal price;
}

