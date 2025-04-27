package com.threadvine.repositories;

import com.threadvine.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, Serializable> {

    List<Comment> findByProductId(UUID product);
}
