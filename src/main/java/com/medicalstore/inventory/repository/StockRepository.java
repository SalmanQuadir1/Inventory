package com.medicalstore.inventory.repository;

import com.medicalstore.inventory.entity.Stock;
import com.medicalstore.inventory.entity.Product;
import com.medicalstore.inventory.entity.Bin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    List<Stock> findByProductId(Long productId);
    List<Stock> findByBinId(Long binId);
    Optional<Stock> findByProductAndBinAndBatchNumber(Product product, Bin bin, String batchNumber);
}
