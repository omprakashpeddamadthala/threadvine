package com.threadvine.service;

import com.threadvine.dto.CommentDTO;

import java.util.List;

public interface CommentService {

     CommentDTO addComment(Long productId, Long userid , CommentDTO commentDTO);
     List<CommentDTO> getCommentsByProductId(Long productId);
}
