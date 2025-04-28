package com.threadvine.repositories;

import com.threadvine.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, Serializable> {

    Optional<Cart> findByUserId(UUID userId);
}
