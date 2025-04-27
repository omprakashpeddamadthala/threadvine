package com.threadvine.repositories;

import com.threadvine.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;

public interface OrderRepository extends JpaRepository<Order, Serializable> {

}
