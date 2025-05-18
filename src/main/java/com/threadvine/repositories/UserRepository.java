package com.threadvine.repositories;

import com.threadvine.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Serializable> {

    Optional<User> findByEmail(String email);
}
