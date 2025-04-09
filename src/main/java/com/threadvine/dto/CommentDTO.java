package com.threadvine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class CommentDTO {

    private Long id;
    private String content;
    private Integer score;
    private Long userId;
    private Long productId;
}
