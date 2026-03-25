package com.medicalstore.inventory.repository;

import com.medicalstore.inventory.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find low stock products
    @Query("SELECT p FROM Product p WHERE p.quantity <= p.reorderLevel")
    List<Product> findLowStockProducts();

    // Find expired products
    @Query("SELECT p FROM Product p WHERE p.expiryDate <= :currentDate")
    List<Product> findExpiredProducts(@Param("currentDate") LocalDate currentDate);

    // Search by name or description
    @Query("SELECT p FROM Product p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.shortDescription) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchProducts(@Param("keyword") String keyword);

    // Search by name or description with pagination AND category
    @Query("SELECT p FROM Product p WHERE (:keyword IS NULL OR :keyword = '' OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.shortDescription) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND (:category IS NULL OR :category = '' OR p.category = :category)")
    Page<Product> searchProductsWithCategory(@Param("keyword") String keyword, @Param("category") String category, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN Stock s ON s.product = p " +
           "LEFT JOIN Bin b ON s.bin = b " +
           "LEFT JOIN Zone z ON b.zone = z " +
           "WHERE (z.warehouse.id IN :warehouseIds OR :warehouseIds IS NULL) " +
           "AND (:keyword IS NULL OR :keyword = '' OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.shortDescription) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:category IS NULL OR :category = '' OR p.category = :category)")
    Page<Product> searchProductsInWarehouses(@Param("keyword") String keyword, @Param("category") String category, @Param("warehouseIds") Collection<Long> warehouseIds, Pageable pageable);

    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.category IS NOT NULL AND p.category != '' ORDER BY p.category")
    List<String> findDistinctCategories();

    // Check if product is used in transactions
    @Query("SELECT COUNT(si) > 0 FROM SaleItem si WHERE si.product.id = :id")
    boolean isUsedInSales(@Param("id") Long id);

    @Query("SELECT COUNT(pi) > 0 FROM PurchaseItem pi WHERE pi.product.id = :id")
    boolean isUsedInPurchases(@Param("id") Long id);

    // Total count of products
    long count();
}
