package com.threadvine.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
    private String address;
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "order" ,cascade = CascadeType.ALL,orphanRemoval = true)
    private List<OrderItem> items =new ArrayList<>();


    public enum OrderStatus {
        PENDING,
        DELIVERING,
        DELIVERED,
        CANCELLED,
    }

}
