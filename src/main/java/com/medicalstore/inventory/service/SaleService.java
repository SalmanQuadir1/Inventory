package com.medicalstore.inventory.service;

import com.medicalstore.inventory.dto.SaleDto;
import java.util.List;

public interface SaleService {
    SaleDto createSale(SaleDto saleDto);
    SaleDto getSaleById(Long id);
    List<SaleDto> getAllSales();
    org.springframework.data.domain.Page<SaleDto> getPaginatedSales(int pageNo, int pageSize, String sortField, String sortDir);
}
