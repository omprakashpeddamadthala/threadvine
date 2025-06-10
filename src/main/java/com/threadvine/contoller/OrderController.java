package com.threadvine.contoller;

import com.threadvine.dto.OrderDTO;
import com.threadvine.model.Order;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@Slf4j
@RestController
@Tag(name = "Orders", description = "Order management APIs")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Create a new order ", description = "Creates a new order")
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

    @Operation(summary = "Retrieve all orders ", description = "Retrieve all orders")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders(){
        log.info( "Received GET  request for get all Orders");
        List<OrderDTO> allOrders = orderService.getAllOrders();
        return new ResponseEntity<>( allOrders,HttpStatus.OK );
    }

    @Operation(summary = "Retrieve order by order id  ", description = "Retrieve order by order id  ")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize( "isAuthenticated()" )
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderByOrderId(@PathVariable UUID orderId){
        log.info( "Received GET  request for get order details by {} ",orderId );
        OrderDTO dto  = orderService.getOrderByOrderId(orderId);
        return new ResponseEntity<>( dto, HttpStatus.OK );
    }


    @Operation(summary = "Retrieve logged in user orders ", description = "Retrieve logged in user orders")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize( "isAuthenticated()" )
    @GetMapping("/user")
    public ResponseEntity<List<OrderDTO>> getUserOrders(@AuthenticationPrincipal UserDetails userDetails){
        log.info( "Received GET  request for get current logged in user orders" );
        UUID userId = ((User) userDetails).getId();
        List<OrderDTO> orderDTOS = orderService.getUserOrders(userId);
        return new ResponseEntity<>( orderDTOS,HttpStatus.OK );
    }


    @Operation(summary = "Update order status  ", description = "Update order status" )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable UUID orderId, @RequestParam Order.OrderStatus status){
        log.info( "Received PUT request for update status {} ",status );
        OrderDTO updatedOrder = orderService.updateOrderStatus(orderId,status);
        return new ResponseEntity<>( updatedOrder,HttpStatus.OK );
    }
}
