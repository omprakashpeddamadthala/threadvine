package com.threadvine.service;

import com.threadvine.dto.CommentDTO;

import java.util.List;
import java.util.UUID;

public interface CommentService {

     CommentDTO addComment(UUID productId, UUID userid , CommentDTO commentDTO);
     List<CommentDTO> getCommentsByProductId(UUID productId);
}
