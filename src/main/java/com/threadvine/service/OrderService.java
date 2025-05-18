package com.threadvine.service;

import com.threadvine.dto.OrderDTO;
import com.threadvine.model.Order;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    OrderDTO createOrder(UUID userId,String address ,String phoneNumber);

    List<OrderDTO> getAllOrders();

    OrderDTO getOrderByOrderId(UUID orderId);

    List<OrderDTO> getUserOrders( UUID userId);

    OrderDTO updateOrderStatus(UUID orderId, Order.OrderStatus status);
}
