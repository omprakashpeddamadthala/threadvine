package com.threadvine.contoller;

import com.threadvine.dto.CartDTO;
import com.threadvine.model.User;
import com.threadvine.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@Tag(name = "Shopping Cart", description = "Shopping cart management APIs")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "Add product to cart", description = "Adds a product to the authenticated user's shopping cart")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product successfully added to cart",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input parameters", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @PostMapping("/add")
    @PreAuthorize( "isAuthenticated()" )
    public ResponseEntity<CartDTO> addToCart(
            @Parameter(description = "Authenticated user details", hidden = true)
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Product ID to add to cart", required = true)
            @RequestParam UUID productId,
            @Parameter(description = "Quantity of product to add", required = true)
            @RequestParam Integer quantity) {
        log.info("Received Post request for product: {} with quantity: {}", productId, quantity );
        UUID userId = ((User) userDetails).getId();
        CartDTO cart = cartService.addToCart( userId, productId, quantity );
        return ResponseEntity.ok( cart );
    }

    @Operation(summary = "Get user's cart", description = "Retrieves the authenticated user's shopping cart")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cart retrieved successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartDTO.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "404", description = "Cart not found", content = @Content)
    })
    @GetMapping
    @PreAuthorize( "isAuthenticated()" )
    public ResponseEntity<CartDTO> getCart(
            @Parameter(description = "Authenticated user details", hidden = true)
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Received Get request for cart");
        UUID userId = ((User) userDetails).getId();
        CartDTO cart = cartService.getCartByUserId( userId );
        return ResponseEntity.ok( cart );
    }

    @Operation(summary = "Clear user's cart", description = "Removes all items from the authenticated user's shopping cart")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cart cleared successfully", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "404", description = "Cart not found", content = @Content)
    })
    @DeleteMapping
    @PreAuthorize( "isAuthenticated()" )
    public ResponseEntity<?> clearCart(
            @Parameter(description = "Authenticated user details", hidden = true)
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Received Clear request for cart");
        UUID userId = ((User) userDetails).getId();
        cartService.clearCart( userId );
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
