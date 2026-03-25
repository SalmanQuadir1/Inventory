package com.medicalstore.inventory.service;

import com.medicalstore.inventory.dto.PurchaseDto;
import java.util.List;

public interface PurchaseService {
    PurchaseDto createPurchase(PurchaseDto purchaseDto);
    PurchaseDto getPurchaseById(Long id);
    List<PurchaseDto> getAllPurchases();
    org.springframework.data.domain.Page<PurchaseDto> getPaginatedPurchases(int pageNo, int pageSize, String sortField, String sortDir);
}
