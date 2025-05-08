package com.threadvine.service.impl;

import com.threadvine.dto.CartDTO;
import com.threadvine.dto.OrderDTO;
import com.threadvine.exceptions.CartNotFoundException;
import com.threadvine.exceptions.InsufficientQuantityException;
import com.threadvine.exceptions.ProductNotFoundException;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

    @Transactional
    public OrderDTO createOrder(UUID userId,String address ,String phoneNumber) {
        log.info( "Creating order for user: {}", userId );

        User user = getUserByUserId( userId);
        CartDTO cartDTO = cartService.getCartByUserId( user.getId() );
        Cart cart = cartMapper.toEntity( cartDTO );

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException( "Cannot create order with empty cart" );
        }

        Order order = Order.builder()
                .user( user )
                .address( address)
                .phoneNumber( phoneNumber )
                .createdAt( LocalDateTime.now() )
                .status( Order.OrderStatus.PENDING )
                .build();

        List<OrderItem> orderItems = createOrderItems(cart,order );
        order.setItems( orderItems );

        Order savedOrder = orderRepository.save( order );
        cartService.clearCart( user.getId() );

        //send email when order is created
        return orderMapper.toDto( savedOrder );
    }

    private List<OrderItem> createOrderItems(Cart cart, Order order) {
        return cart.getItems().stream().map( cartItem -> {

            Product product = getProductByProductId(cartItem.getProduct().getId());

            if(product.getQuantity() == null)
                throw new IllegalArgumentException("Product quantity not set for produce "+product.getName());

            if(product.getQuantity()<cartItem.getQuantity())
                throw new InsufficientQuantityException( "Not enough stock for product "+product.getName() );

            product.setQuantity( product.getQuantity() - cartItem.getQuantity() );

            productRepository.save( product );
            return new OrderItem(order,product,cartItem.getQuantity(),product.getPrice());
       }
        ).collect( Collectors.toList() );

    }

    private Product getProductByProductId(UUID productId) {
        return productRepository.findById( productId )
                .orElseThrow( () -> new ProductNotFoundException( "Product not found with id: " + productId ) );
    }


    private User getUserByUserId(UUID userId) {
        return userRepository.findById( userId ).orElseThrow( () -> new UserNotFoundException( "User not found with id: " + userId ) );
    }
}
