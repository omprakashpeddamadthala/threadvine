package com.threadvine.mappers;

import com.threadvine.dto.CartDTO;
import com.threadvine.dto.CartItemDTO;
import com.threadvine.model.Cart;
import com.threadvine.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping( target = "userId" ,source = "user.id")
    CartDTO toDto(Cart cart);

    @Mapping( target = "user.id" ,source = "userId")
    Cart toEntity(CartDTO cartDTO);

    @Mapping( target = "productId" ,source = "product.id")
    CartItemDTO toCartItemDto(CartItem cartItem);

    @Mapping( target = "product.id" ,source = "productId")
    CartItem toCartItemEntity(CartItemDTO cartItemDTO);

}
