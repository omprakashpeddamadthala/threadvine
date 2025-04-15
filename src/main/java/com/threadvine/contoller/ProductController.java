package com.threadvine.contoller;

import com.threadvine.dto.ProductDTO;
import com.threadvine.dto.ProductListDTO;
import com.threadvine.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid  @RequestBody  ProductDTO productDTO, MultipartFile file) {
        log.info( "Received product creation request for product: {}", productDTO.getName());
        ProductDTO createdProduct = productService.createProduct( productDTO, file );
        return new ResponseEntity<>( createdProduct,HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@Valid @RequestBody ProductDTO productDTO,@PathVariable Long id,  MultipartFile file) {
        log.info( "Received product update request for product: {}", productDTO.getName());
        ProductDTO updatedProduct = productService.updateProduct( productDTO,file ,id );
        return new ResponseEntity<>( updatedProduct,HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long id) {
        log.info( "Received product delete request for product: {}", id);
        ProductDTO deletedProduct = productService.deleteProduct( id );
        return new ResponseEntity<>( deletedProduct,HttpStatus.NO_CONTENT);
    }

    @GetMapping ("/{id}")
    public ResponseEntity<ProductListDTO> getProductById(@PathVariable Long id) {
        log.info( "Received product get request for product: {}", id);
        ProductListDTO product = productService.getProductById( id );
        return new ResponseEntity<>( product,HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ProductListDTO>> getAllProducts() {
        log.info( "Received product get request for all products" );
        List<ProductListDTO> products = productService.getAllProducts();
        return new ResponseEntity<>( products,HttpStatus.OK);
    }
}
