package com.threadvine.service.impl;

import com.threadvine.dto.CommentDTO;
import com.threadvine.exceptions.ProductNotFoundException;
import com.threadvine.mappers.CommentMapper;
import com.threadvine.model.Comment;
import com.threadvine.model.Product;
import com.threadvine.model.User;
import com.threadvine.repositories.CommentRepository;
import com.threadvine.repositories.ProductRepository;
import com.threadvine.repositories.UserRepository;
import com.threadvine.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    public CommentDTO addComment(UUID productId, UUID userid , CommentDTO commentDTO) {
        log.info( "Adding comment to product and userid: {}", productId, userid );

        Product product = this.getProductByProductId( productId );

        User user = userRepository.findById( userid )
                .orElseThrow( () -> new RuntimeException( "User not found with id: " + userid)  );

        Comment comment = commentMapper.toEntity( commentDTO );
        comment.setProduct( product );
        comment.setUser( user );

        Comment savedComment = commentRepository.save( comment );
        log.info( "Comment added: {}", savedComment.getContent());
        return commentMapper.toDto( savedComment );
    }

    public List<CommentDTO> getCommentsByProductId(UUID productId) {
        log.info( "Getting comment by product id: {}", productId );
        List<Comment> comment = commentRepository.findByProductId( productId );
        return comment.stream()
                .map( commentMapper::toDto )
                .collect( Collectors.toList() );
    }

    private Product getProductByProductId(UUID productId) {
        return productRepository.findById( productId )
                .orElseThrow( () -> new ProductNotFoundException( "Product not found with id: " + productId ) );
    }




}
