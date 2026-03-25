package com.medicalstore.inventory.service;

import com.medicalstore.inventory.dto.SaleDto;
import org.springframework.data.domain.Page;
import java.util.List;

public interface SaleService {
    SaleDto createSale(SaleDto saleDto);
    SaleDto getSaleById(Long id);
    List<SaleDto> getAllSales();
    Page<SaleDto> getPaginatedSales(int pageNo, int pageSize, String sortField, String sortDir);
}
