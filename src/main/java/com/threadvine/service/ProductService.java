package com.threadvine.service;

import com.threadvine.dto.ProductDTO;
import com.threadvine.dto.ProductListDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductDTO createProduct(ProductDTO productDTO, MultipartFile file);

    ProductDTO updateProduct(ProductDTO productDTO, MultipartFile file, UUID productId);

    ProductDTO deleteProduct(UUID id);

    ProductListDTO getProductById(UUID id);

    List<ProductListDTO> getAllProducts();
}
