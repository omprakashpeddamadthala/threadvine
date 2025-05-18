package com.threadvine.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product  extends BaseEntity {

    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private String imageUrl;


    @OneToMany(mappedBy = "product" ,cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Comment> comments =new ArrayList<>();

}
