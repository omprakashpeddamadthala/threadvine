package com.threadvine.service.impl;

import com.threadvine.dto.ProductDTO;
import com.threadvine.dto.ProductListDTO;
import com.threadvine.mappers.ProductMapper;
import com.threadvine.model.Product;
import com.threadvine.repositories.ProductRepository;
import com.threadvine.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    public static final String PRODUCT_IMAGE_PATH = "src/main/resources/static/Images/";

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductDTO createProduct(ProductDTO productDTO, MultipartFile file) {
        log.info( "Creating product: {}", productDTO.getName() );

        if(productRepository.existsByName(productDTO.getName()))
            throw new RuntimeException("Product with name " + productDTO.getName() + " already exists");

        Product product = productMapper.toEntity( productDTO );
        if (file != null && !file.isEmpty()) {
            String fileName = this.saveProductImage( file );
            product.setImageUrl( "/images/" + fileName );
        }

        Product savedProduct = productRepository.save( product );

        log.info( "Product created: {}", savedProduct.getName() );
        return productMapper.toDto( savedProduct );
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, MultipartFile file, Long productId) {
        log.info( "Updating product: {}", productDTO.getName() );

        Product product = productRepository.findById( productId )
                .orElseThrow( () -> new RuntimeException( "Product not found with id: " + productId)  );

        product.setName( productDTO.getName() );
        product.setDescription( productDTO.getDescription() );
        product.setPrice( productDTO.getPrice() );
        product.setQuantity( productDTO.getQuantity() );

        if (file != null && !file.isEmpty()) {
            String fileName = this.saveProductImage( file );
            product.setImageUrl( "/images/" + fileName );
        }

        Product updatedProduct = productRepository.save( product );

        log.info( "Product updated: {}", updatedProduct.getName() );
        return productMapper.toDto( updatedProduct );
    }

    @Override
    public ProductDTO deleteProduct(Long id) {
        log.info( "Deleting product with id: {}", id );
        Product product = productRepository.findById( id )
                .orElseThrow( () -> new RuntimeException( "Product not found with id: " + id)  );
        productRepository.delete( product );
        return null;
    }

    @Override
    public ProductListDTO getProductById(Long id) {
        log.info( "Getting product with id: {}", id );
        Product product = productRepository.findById( id )
                .orElseThrow( () -> new RuntimeException( "Product not found with id: " + id)  );
        return productMapper.toProductListDTO( product );
    }

    @Override
    public List<ProductListDTO> getAllProducts() {
        log.info( "Getting all products" );
        return  productRepository.findAll()
                .stream().map(productMapper::toProductListDTO).toList();
    }

    private String saveProductImage(MultipartFile file) {
        log.info( "Saving product image to server file name: {}", file.getOriginalFilename());
        String fileName = file.getOriginalFilename();
        try {
            file.transferTo( new java.io.File( PRODUCT_IMAGE_PATH + fileName ) );
        } catch (Exception e) {
            throw new RuntimeException( "Error saving product image", e );
        }
        return fileName;
    }
}
