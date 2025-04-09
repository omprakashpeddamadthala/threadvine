package com.threadvine.mappers;

import com.threadvine.dto.OrderDTO;
import com.threadvine.dto.OrderItemDTO;
import com.threadvine.model.Order;
import com.threadvine.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping( target = "userId" , source = "user.id")
    OrderDTO toDto(Order order);

    @Mapping( target = "user.id" , source = "userId")
    Order toEntity(OrderDTO orderDTO);

    @Mapping( target = "productId" , source = "product.id")
    OrderItemDTO toOrderItemDto(OrderItem orderItem);

    @Mapping( target = "product.id" , source = "productId")
    OrderItem toOrderItemEntity(OrderItemDTO orderItemDTO);

    List<OrderItemDTO> toOrderItemDTOs(List<OrderItem> orderItems);

    List<OrderItem> toOrderItemEntities(List<OrderItemDTO> orderItemDTOs);

    List<OrderDTO> toOrderDTOs(List<Order> orders);

    List<Order> toOrderEntities(List<OrderDTO> orderDTOs);
}
