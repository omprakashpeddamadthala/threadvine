package com.threadvine.service;

import com.threadvine.dto.ProductDTO;
import com.threadvine.dto.ProductListDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    ProductDTO createProduct(ProductDTO productDTO, MultipartFile file);

    ProductDTO updateProduct(ProductDTO productDTO, MultipartFile file, Long productId);

    ProductDTO deleteProduct(Long id);

    ProductListDTO getProductById(Long id);

    List<ProductListDTO> getAllProducts();
}
