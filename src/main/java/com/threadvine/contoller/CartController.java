package com.threadvine.contoller;

import com.threadvine.dto.CartDTO;
import com.threadvine.model.User;
import com.threadvine.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    @PreAuthorize( "isAuthenticated()" )
    public ResponseEntity<CartDTO> addToCart(@AuthenticationPrincipal UserDetails userDetails,
                                             @RequestParam UUID productId,
                                             @RequestParam Integer quantity) {
        log.info("Received Post request for product: {} with quantity: {}", productId, quantity );
        UUID userId = ((User) userDetails).getId();
        CartDTO cart = cartService.addToCart( userId, productId, quantity );
        return ResponseEntity.ok( cart );
    }

    @GetMapping
    @PreAuthorize( "isAuthenticated()" )
    public ResponseEntity<CartDTO> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Received Get request for cart");
        UUID userId = ((User) userDetails).getId();
        CartDTO cart = cartService.getCartByUserId( userId );
        return ResponseEntity.ok( cart );
    }

    @DeleteMapping
    @PreAuthorize( "isAuthenticated()" )
    public ResponseEntity<?> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Received Clear request for cart");
        UUID userId = ((User) userDetails).getId();
        cartService.clearCart( userId );
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
