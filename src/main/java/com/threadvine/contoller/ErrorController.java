package com.threadvine.contoller;

import com.threadvine.dto.ApiErrorResponse;
import com.threadvine.exceptions.InsufficientQuantityException;
import com.threadvine.exceptions.ProductNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ErrorController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception e) {
        log.error("Exception occurred", e);
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("An unexpected error occurred")
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        log.error("Exception occurred", e);
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message("You don't have access to this API")
                .build();
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException occurred", e);
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalStateException(IllegalStateException e) {
        log.error("IllegalStateException occurred", e);
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InsufficientQuantityException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentialsException(InsufficientQuantityException e) {
        log.error( "InsufficientQuantityException occurred", e );
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status( HttpStatus.BAD_REQUEST.value() )
                .message( e.getMessage() )
                .build();
        return new ResponseEntity<>( error, HttpStatus.BAD_REQUEST );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
        log.error( "BadCredentialsException occurred", e );
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status( HttpStatus.UNAUTHORIZED.value() )
                .message( "Incorrect username or password" )
                .build();
        return new ResponseEntity<>( error, HttpStatus.UNAUTHORIZED );
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleProductNotFoundException(ProductNotFoundException e) {
        log.error( "ProductNotFoundException occurred", e );
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status( HttpStatus.NOT_FOUND.value() )
                .message( e.getMessage() )
                .build();
        return new ResponseEntity<>( error, HttpStatus.NOT_FOUND );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handle(MethodArgumentNotValidException exp) {
        List<ApiErrorResponse.FieldError> fieldErrors = exp.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> ApiErrorResponse.FieldError.builder()
                        .field(((org.springframework.validation.FieldError) error).getField())
                        .message(error.getDefaultMessage())
                        .build())
                .collect( Collectors.toList());

        ApiErrorResponse response = ApiErrorResponse.builder()
                .status( HttpStatus.BAD_REQUEST.value())
                .message("Validation failed")
                .errors(fieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
