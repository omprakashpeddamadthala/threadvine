package com.threadvine.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDTO {

    private UUID id;
    @NotBlank(message = "content is required")
    private String content;
    @Min( value = 1)
    @Max( value = 5)
    private Integer score;
    private UUID userId;
}
