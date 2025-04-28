package com.threadvine.service;

import com.threadvine.dto.CartDTO;

import java.util.UUID;

public interface CartService {

    CartDTO addToCart(UUID userId, UUID productId, Integer quantity);

    CartDTO getCartByUserId(UUID userId);

    void clearCart(UUID userId);
}
