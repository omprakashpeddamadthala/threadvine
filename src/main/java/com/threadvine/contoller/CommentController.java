package com.threadvine.contoller;

import com.threadvine.dto.CommentDTO;
import com.threadvine.model.User;
import com.threadvine.service.CommentService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
@Slf4j
@RestController
@Tag(name = "Comments", description = "Product comments management APIs")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "Add a comment to a product", description = "Creates a new comment for a specific product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Comment successfully created",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/product/{productId}")
    @PreAuthorize( "isAuthenticated()" )
    public ResponseEntity<CommentDTO> addComment(
            @Parameter(description = "ID of the product to comment on", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Authenticated user details", hidden = true)
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Comment details", required = true)
            @Valid @RequestBody CommentDTO commentDTO) {
        log.info( "Received comment add request for product: {}", productId );
        UUID userid = ((User) userDetails).getId();
        CommentDTO comment = commentService.addComment( productId, userid, commentDTO );
        return new ResponseEntity<>( comment, HttpStatus.CREATED );
    }

    @Operation(summary = "Get comments for a product", description = "Retrieves all comments for a specific product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comments retrieved successfully",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = CommentDTO.class, type = "array"))),
        @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByProductId(
            @Parameter(description = "ID of the product to get comments for", required = true)
            @PathVariable UUID productId) {
        log.info( "Received comment get request for product: {}", productId );
        List<CommentDTO> comments = commentService.getCommentsByProductId( productId );
        return new ResponseEntity<>( comments, HttpStatus.OK );
    }

}
