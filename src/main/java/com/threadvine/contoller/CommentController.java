package com.threadvine.contoller;

import com.threadvine.dto.CommentDTO;
import com.threadvine.model.User;
import com.threadvine.service.CommentService;
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
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/product/{productId}")
    @PreAuthorize( "isAuthenticated()" )
    public ResponseEntity<CommentDTO> addComment(@PathVariable UUID productId,
                                                @AuthenticationPrincipal UserDetails userDetails,
                                                @Valid @RequestBody CommentDTO commentDTO) {
        log.info( "Received comment add request for product: {}", productId );
        UUID userid = ((User) userDetails).getId();
        CommentDTO comment = commentService.addComment( productId, userid, commentDTO );
        return new ResponseEntity<>( comment, HttpStatus.CREATED );
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByProductId(@PathVariable UUID productId) {
        log.info( "Received comment get request for product: {}", productId );
        List<CommentDTO> comments = commentService.getCommentsByProductId( productId );
        return new ResponseEntity<>( comments, HttpStatus.OK );
    }

}
