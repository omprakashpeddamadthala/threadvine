package com.threadvine.service;

import com.threadvine.dto.OrderDTO;

import java.util.UUID;

public interface OrderService {

    OrderDTO createOrder(UUID userId,String address ,String phoneNumber);
}
