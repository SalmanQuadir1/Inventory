package com.medicalstore.inventory.service;

import com.medicalstore.inventory.dto.ProductDto;
import java.util.List;

public interface ProductService {
    ProductDto createProduct(ProductDto productDto);

    ProductDto updateProduct(Long id, ProductDto productDto);

    void deleteProduct(Long id);

    ProductDto getProductById(Long id);

    List<ProductDto> getAllProducts();

    List<ProductDto> getLowStockProducts();

    List<ProductDto> getExpiredProducts();

    List<ProductDto> searchProducts(String keyword);

    void updateStock(Long id, int quantityChange);

    void bulkUploadProducts(org.springframework.web.multipart.MultipartFile file) throws Exception;

    List<String> getDistinctCategories();

    org.springframework.data.domain.Page<ProductDto> getPaginatedProducts(int pageNo, int pageSize, String sortField, String sortDir, String keyword, String category);
}
