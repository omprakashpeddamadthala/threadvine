package com.threadvine.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class ProductDTO {

    private UUID id;
    @NotBlank(message = "Product name is required")
    private String name;
    @NotBlank(message = "Product description is required")
    private String description;
    @Positive(message = "Cannot be negative")
    private BigDecimal price;
    @PositiveOrZero(message = "Cannot be negative or zero")
    private Integer quantity;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String imageUrl;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<CommentDTO> comments;

}
