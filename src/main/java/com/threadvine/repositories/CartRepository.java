package com.threadvine.repositories;

import com.threadvine.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;

public interface CartRepository extends JpaRepository<Cart, Serializable> {

}
