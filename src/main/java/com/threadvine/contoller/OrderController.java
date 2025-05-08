package com.threadvine.contoller;

import com.threadvine.dto.OrderDTO;
import com.threadvine.dto.ProductDTO;
import com.threadvine.model.User;
import com.threadvine.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@Slf4j
@RestController
@Tag(name = "Orders", description = "Order management APIs")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Create a new order ", description = "Creates a new order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order successfully placed",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires USER role", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize( "isAuthenticated()" )
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@AuthenticationPrincipal UserDetails userDetails,
                                                @Parameter(description = "Address is required", required = true)
                                                @RequestParam String address,
                                                @Parameter(description = "phoneNumber is required", required = true)
                                                @RequestParam String phoneNumber) {
        log.info( "Received POST Request for creating order for username {}", userDetails.getUsername() );
        UUID userId = ((User) userDetails).getId();
        OrderDTO dto = orderService.createOrder( userId ,address,phoneNumber );
        return new ResponseEntity<>( dto, HttpStatus.CREATED );
    }
}
