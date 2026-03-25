package com.medicalstore.inventory.repository;

import com.medicalstore.inventory.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    @Query("SELECT s FROM Sale s WHERE s.saleDate >= :startDate AND s.saleDate <= :endDate")
    List<Sale> findSalesBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(s.totalAmount) FROM Sale s")
    java.math.BigDecimal getTotalRevenue();

    @Query("SELECT SUM((si.price - p.purchasePrice) * si.quantity) FROM SaleItem si JOIN si.product p")
    java.math.BigDecimal getTotalProfit();

    @Query("SELECT NEW com.medicalstore.inventory.dto.AnalyticsDto$MonthlyData(" +
           "FUNCTION('DATE_FORMAT', s.saleDate, '%Y-%m'), SUM(s.totalAmount), " +
           "SUM((si.price - p.purchasePrice) * si.quantity)) " +
           "FROM Sale s JOIN s.items si JOIN si.product p " +
           "GROUP BY FUNCTION('DATE_FORMAT', s.saleDate, '%Y-%m') " +
           "ORDER BY FUNCTION('DATE_FORMAT', s.saleDate, '%Y-%m') DESC")
    List<com.medicalstore.inventory.dto.AnalyticsDto.MonthlyData> getMonthlyAnalytics();

    @Query("SELECT NEW com.medicalstore.inventory.dto.AnalyticsDto$WarehousePerformance(" +
           "w.name, SUM(s.totalAmount), SUM((si.price - p.purchasePrice) * si.quantity), COUNT(DISTINCT s.id)) " +
           "FROM Sale s JOIN s.store w JOIN s.items si JOIN si.product p " +
           "GROUP BY w.name")
    List<com.medicalstore.inventory.dto.AnalyticsDto.WarehousePerformance> getWarehousePerformance();

    @Query(value = "SELECT p.product_name as productName, SUM(si.quantity) as quantitySold, " +
           "SUM(si.quantity * si.price) as revenue, " +
           "SUM(si.quantity * (si.price - p.purchase_price)) as profit " +
           "FROM sale_items si JOIN products p ON si.product_id = p.id " +
           "GROUP BY p.id, p.product_name ORDER BY quantitySold DESC LIMIT 10", nativeQuery = true)
    List<Object[]> getTopSellingProductsNative();
}
