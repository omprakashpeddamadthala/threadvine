package com.threadvine.service.impl;

import com.threadvine.dto.CartDTO;
import com.threadvine.dto.OrderDTO;
import com.threadvine.exceptions.CartNotFoundException;
import com.threadvine.exceptions.UserNotFoundException;
import com.threadvine.mappers.CartMapper;
import com.threadvine.mappers.OrderMapper;
import com.threadvine.model.*;
import com.threadvine.repositories.CartRepository;
import com.threadvine.repositories.OrderRepository;
import com.threadvine.repositories.ProductRepository;
import com.threadvine.repositories.UserRepository;
import com.threadvine.service.CartService;
import com.threadvine.service.EmailService;
import com.threadvine.service.OrderService;
import com.threadvine.service.impl.EmailServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final CartMapper cartMapper;

    public OrderDTO createOrder(OrderDTO orderDTO) {
        log.info( "Creating order for user: {}", orderDTO.getUserId() );

        User user = getUserByUserId( orderDTO.getUserId() );
        CartDTO cartDTO = cartService.getCartByUserId( user.getId() );
        Cart cart = cartMapper.toEntity( cartDTO );

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException( "Cannot create order with empty cart" );
        }

        Order order = Order.builder().user( user ).address( orderDTO.getAddress() ).phoneNumber( orderDTO.getPhoneNumber() ).createdAt( LocalDateTime.now() ).status( Order.OrderStatus.PENDING ).build();

        List<OrderItem> orderItems = orderMapper.toOrderItemEntities( orderDTO.getItems() );
        order.setItems( orderItems );

        Order savedOrder = orderRepository.save( order );
        cartService.clearCart( user.getId() );

        //send email when order is created
        return orderMapper.toDto( savedOrder );
    }

    private User getUserByUserId(UUID userId) {
        return userRepository.findById( userId ).orElseThrow( () -> new UserNotFoundException( "User not found with id: " + userId ) );
    }
}
