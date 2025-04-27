package com.threadvine.mappers;

import com.threadvine.dto.OrderDTO;
import com.threadvine.dto.OrderItemDTO;
import com.threadvine.model.Order;
import com.threadvine.model.OrderItem;
import com.threadvine.model.Product;
import com.threadvine.model.User;
import org.mapstruct.*;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping( target = "userId" , source = "user.id")
    OrderDTO toDto(Order order);

    @Mapping(target = "user", source = "userId", qualifiedByName = "mapUserIdToUser")
    Order toEntity(OrderDTO orderDTO);

    @Mapping( target = "productId" , source = "product.id")
    OrderItemDTO toOrderItemDto(OrderItem orderItem);

    @Mapping(target = "product", source = "productId", qualifiedByName = "mapProductIdToProduct")
    OrderItem toOrderItemEntity(OrderItemDTO orderItemDTO);

    List<OrderItemDTO> toOrderItemDTOs(List<OrderItem> orderItems);

    List<OrderItem> toOrderItemEntities(List<OrderItemDTO> orderItemDTOs);

    List<OrderDTO> toOrderDTOs(List<Order> orders);

    List<Order> toOrderEntities(List<OrderDTO> orderDTOs);

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

    @AfterMapping
    default void setOrderInItems(@MappingTarget Order order) {
        for (OrderItem item : order.getItems()) {
            item.setOrder(order);
        }
    }

}
