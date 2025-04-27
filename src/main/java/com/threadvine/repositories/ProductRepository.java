package com.threadvine.repositories;

import com.threadvine.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;

public interface ProductRepository extends JpaRepository<Product, Serializable> {
    boolean existsByName(String name);
}
