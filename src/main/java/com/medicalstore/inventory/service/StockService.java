package com.medicalstore.inventory.service;

import com.medicalstore.inventory.entity.Stock;
import com.medicalstore.inventory.entity.StockTransaction;

import java.time.LocalDate;
import java.util.List;

public interface StockService {
    
    // Core Stock Operations
    void receiveStock(Long productId, Long toBinId, Integer quantity, String batchNumber, LocalDate expiryDate, String reference);
    
    void deductStock(Long productId, Long fromBinId, Integer quantity, String reference);
    
    // Automatically deducts from available bins based on arbitrary order 
    void deductStockAuto(Long productId, Integer quantity, String reference, Long warehouseId);
    
    void transferStock(Long productId, Long fromBinId, Long toBinId, Integer quantity, String reference);
    
    // Queries
    List<Stock> getStockByProduct(Long productId);
    
    List<Stock> getStockByBin(Long binId);
    
    List<StockTransaction> getTransactionsByProduct(Long productId);
    
    List<StockTransaction> getTransactionsByReference(String reference);
}
