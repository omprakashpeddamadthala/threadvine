package com.threadvine.repositories;

import com.threadvine.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, Serializable> {

    List<Order> findByUserId(UUID userId);
}
