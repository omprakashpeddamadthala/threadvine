package com.threadvine.service.impl;

import com.threadvine.dto.CartDTO;
import com.threadvine.exceptions.CartNotFoundException;
import com.threadvine.exceptions.InsufficientQuantityException;
import com.threadvine.exceptions.ProductNotFoundException;
import com.threadvine.exceptions.UserNotFoundException;
import com.threadvine.mappers.CartMapper;
import com.threadvine.model.Cart;
import com.threadvine.model.CartItem;
import com.threadvine.model.Product;
import com.threadvine.model.User;
import com.threadvine.repositories.CartRepository;
import com.threadvine.repositories.ProductRepository;
import com.threadvine.repositories.UserRepository;
import com.threadvine.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.naming.InsufficientResourcesException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    @Override
    public CartDTO addToCart(UUID userId, UUID productId, Integer quantity) {
        log.info( "Adding product to cart with userId: {}, productId: {}, quantity: {}", userId, productId, quantity );

        Product product = this.getProductByProductId( productId );

        User user = this.getUserByUserId( userId );

        if (product.getQuantity() < quantity) {
            throw new InsufficientQuantityException( "Only " + product.getQuantity() + " available quantity" );
        } else if (product.getQuantity() == 0) {
            throw new InsufficientQuantityException( "Out of stock when stock is available will notify" );
        }

        Cart cart = cartRepository.findByUserId( userId )
                .orElse( new Cart( user, new ArrayList<>() ) );

        Optional<CartItem> existingCartItem = cart.getItems().stream()
                .filter( item -> item.getProduct().getId().equals( productId ) )
                .findFirst();

        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity( cartItem.getQuantity() + quantity );
        } else {
            CartItem cartItem = new CartItem( cart, product, quantity );
            cart.getItems().add( cartItem );
        }

        Cart savedCart = cartRepository.save( cart );
        log.info( "Product added to cart: {}", savedCart.getItems() );
        return cartMapper.toDto( savedCart );
    }

    @Override
    public CartDTO getCartByUserId(UUID userId) {
        log.info( "Getting cart by userId: {}", userId );

        Cart cart = this.getCartByCartId( userId );
        return cartMapper.toDto( cart );
    }

    @Override
    public void clearCart(UUID userId) {
        log.info( "Clearing cart by userId: {}", userId );

        Cart cart = this.getCartByCartId( userId );
        cart.getItems().clear();
        cartRepository.save( cart );
    }

    private Product getProductByProductId(UUID productId) {
        return productRepository.findById( productId )
                .orElseThrow( () -> new ProductNotFoundException("Product not found with id: " + productId ) );
    }

    private Cart getCartByCartId(UUID userId) {
        return cartRepository.findByUserId( userId )
                .orElseThrow( () -> new CartNotFoundException("Cart not found with id: " + userId ) );
    }

    private User getUserByUserId(UUID userId) {
        return userRepository.findById( userId )
                .orElseThrow( () -> new UserNotFoundException("User not found with id: " + userId ) );
    }

}
