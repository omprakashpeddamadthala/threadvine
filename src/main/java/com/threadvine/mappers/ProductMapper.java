package com.threadvine.mappers;

import com.threadvine.dto.ProductDTO;
import com.threadvine.dto.ProductListDTO;
import com.threadvine.model.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",uses = {CommentMapper.class})
public interface ProductMapper {

    ProductDTO toDto(Product product);

    Product toEntity(ProductDTO productDTO);

    ProductListDTO toProductListDTO(Product product);
}
