package com.medicalstore.inventory.service;

import com.medicalstore.inventory.dto.SupplierDto;
import java.util.List;

public interface SupplierService {
    SupplierDto createSupplier(SupplierDto supplierDto);
    SupplierDto updateSupplier(Long id, SupplierDto supplierDto);
    void deleteSupplier(Long id);
    SupplierDto getSupplierById(Long id);
    List<SupplierDto> getAllSuppliers();
    org.springframework.data.domain.Page<SupplierDto> getPaginatedSuppliers(int pageNo, int pageSize, String sortField, String sortDir);
}
