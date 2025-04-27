package com.threadvine.mappers;

import com.threadvine.dto.CartDTO;
import com.threadvine.dto.CartItemDTO;
import com.threadvine.model.Cart;
import com.threadvine.model.CartItem;
import com.threadvine.model.Product;
import com.threadvine.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping( target = "userId" ,source = "user.id")
    CartDTO toDto(Cart cart);

    @Mapping( target = "user" ,source = "userId", qualifiedByName = "mapUserIdToUser")
    Cart toEntity(CartDTO cartDTO);

    @Mapping( target = "productId" ,source = "product.id")
    CartItemDTO toCartItemDto(CartItem cartItem);

    @Mapping( target = "product" ,source = "productId", qualifiedByName = "mapProductIdToProduct")
    CartItem toCartItemEntity(CartItemDTO cartItemDTO);

    @Named("mapUserIdToUser")
    default User mapUserIdToUser(UUID userId) {
        User user = new User();
        user.setId(userId);
        return user;
    }

    // Custom mapping for productId to Product object
    @Named("mapProductIdToProduct")
    default Product mapProductIdToProduct(UUID productId) {
        Product product = new Product();
        product.setId(productId);
        return product;
    }


}
