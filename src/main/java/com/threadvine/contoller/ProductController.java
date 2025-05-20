package com.threadvine.contoller;

import com.threadvine.dto.ProductDTO;
import com.threadvine.dto.ProductListDTO;
import com.threadvine.service.ProductService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product management APIs")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Create a new product", description = "Creates a new product with optional image upload")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN','SELLER')")
    public ResponseEntity<ProductDTO> createProduct(
            @Parameter(description = "Product details", required = true)
            @RequestPart("product") @Valid ProductDTO productDTO,
            @Parameter(description = "Product image file (optional)")
            @RequestPart(value = "image", required = false) MultipartFile file) {
        log.info( "Received product creation request for product: {}", productDTO.getName());
        ProductDTO createdProduct = productService.createProduct( productDTO, file );
        return new ResponseEntity<>( createdProduct,HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing product", description = "Updates a product with the given ID and optional new image")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    public ResponseEntity<ProductDTO> updateProduct(
            @Parameter(description = "ID of the product to update", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Updated product details", required = true)
            @RequestPart("product") @Valid ProductDTO productDTO,
            @Parameter(description = "Updated product image (optional)")
            @RequestPart(value = "image", required = false) MultipartFile image) {
        log.info( "Received product update request for product: {}", productDTO.getName());
        ProductDTO updatedProduct = productService.updateProduct( productDTO, image, id );
        return new ResponseEntity<>( updatedProduct, HttpStatus.OK);
    }

    @Operation(summary = "Delete a product", description = "Deletes a product with the given ID")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    public ResponseEntity<ProductDTO> deleteProduct(
            @Parameter(description = "ID of the product to delete", required = true)
            @PathVariable UUID id) {
        log.info( "Received product delete request for product: {}", id);
        ProductDTO deletedProduct = productService.deleteProduct( id );
        return new ResponseEntity<>( deletedProduct, HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Get a product by ID", description = "Retrieves a product with the given ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(
            @Parameter(description = "ID of the product to retrieve", required = true)
            @PathVariable UUID id) {
        log.info( "Received product get request for product: {}", id);
        ProductDTO product = productService.getProductById( id );
        return new ResponseEntity<>( product, HttpStatus.OK);
    }

    @Operation(summary = "Get all products", description = "Retrieves a list of all available products")
    @GetMapping
    public ResponseEntity<List<ProductListDTO>> getAllProducts() {
        log.info( "Received product get request for all products" );
        List<ProductListDTO> products = productService.getAllProducts();
        return new ResponseEntity<>( products, HttpStatus.OK );
    }
}
