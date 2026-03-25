package com.medicalstore.inventory.service.impl;

import com.medicalstore.inventory.dto.PurchaseDto;
import com.medicalstore.inventory.dto.PurchaseItemDto;
import com.medicalstore.inventory.entity.Product;
import com.medicalstore.inventory.entity.Purchase;
import com.medicalstore.inventory.entity.PurchaseItem;
import com.medicalstore.inventory.entity.Supplier;
import com.medicalstore.inventory.exception.ResourceNotFoundException;
import com.medicalstore.inventory.repository.ProductRepository;
import com.medicalstore.inventory.repository.PurchaseRepository;
import com.medicalstore.inventory.repository.SupplierRepository;
import com.medicalstore.inventory.repository.UserRepository;
import com.medicalstore.inventory.entity.Warehouse;
import com.medicalstore.inventory.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@org.springframework.transaction.annotation.Transactional
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final com.medicalstore.inventory.service.StockService stockService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    @SuppressWarnings("null")
    public PurchaseDto createPurchase(PurchaseDto dto) {
        Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));

        Purchase purchase = new Purchase();
        purchase.setSupplier(supplier);
        purchase.setPurchaseDate(dto.getPurchaseDate());

        // ERP Extensions
        purchase.setInvoiceNumber(dto.getInvoiceNumber());
        purchase.setInvoiceDate(dto.getInvoiceDate());
        purchase.setDiscountAmount(dto.getDiscountAmount());
        purchase.setGstAmount(dto.getGstAmount());

        Warehouse store = getCurrentUserStore();
        purchase.setStore(store);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (PurchaseItemDto itemDto : dto.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            PurchaseItem item = new PurchaseItem();
            item.setProduct(product);
            item.setQuantity(itemDto.getQuantity());
            BigDecimal priceToUse = itemDto.getPrice() != null ? itemDto.getPrice() : BigDecimal.ZERO;
            item.setPrice(priceToUse);

            purchase.addItem(item);

            totalAmount = totalAmount.add(priceToUse.multiply(new BigDecimal(itemDto.getQuantity())));

            // Update inventory via WMS StockService
            if (itemDto.getBinId() != null) {
                String reference = purchase.getInvoiceNumber() != null ? "PO-" + purchase.getInvoiceNumber() : "PO-SYS-" + System.currentTimeMillis();
                stockService.receiveStock(
                    itemDto.getProductId(), 
                    itemDto.getBinId(), 
                    itemDto.getQuantity(), 
                    itemDto.getBatchNumber(), 
                    itemDto.getExpiryDate(), 
                    reference
                );
            }

            int currentStock = product.getQuantity() != null ? product.getQuantity() : 0;
            product.setQuantity(currentStock + itemDto.getQuantity());
            productRepository.save(product);
        }

        purchase.setTotalAmount(totalAmount);
        Purchase savedPurchase = purchaseRepository.save(purchase);

        return mapToDto(savedPurchase);
    }

    @Override
    @SuppressWarnings("null")
    public PurchaseDto getPurchaseById(Long id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found"));
        return mapToDto(purchase);
    }

    @Override
    public List<PurchaseDto> getAllPurchases() {
        return purchaseRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("null")
    public org.springframework.data.domain.Page<PurchaseDto> getPaginatedPurchases(int pageNo, int pageSize, String sortField, String sortDir) {
        org.springframework.data.domain.Sort sort = sortDir.equalsIgnoreCase("asc") ? 
            org.springframework.data.domain.Sort.by(sortField).ascending() : 
            org.springframework.data.domain.Sort.by(sortField).descending();
            
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(pageNo - 1, pageSize, sort);
        
        org.springframework.data.domain.Page<Purchase> page = purchaseRepository.findAll(pageable);
        
        List<PurchaseDto> dtoList = page.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
                
        return new org.springframework.data.domain.PageImpl<>(dtoList, pageable, page.getTotalElements());
    }

    private PurchaseDto mapToDto(Purchase purchase) {
        PurchaseDto dto = new PurchaseDto();
        dto.setId(purchase.getId());
        dto.setSupplierId(purchase.getSupplier().getId());
        dto.setSupplierName(purchase.getSupplier().getSupplierName());
        dto.setPurchaseDate(purchase.getPurchaseDate());
        dto.setTotalAmount(purchase.getTotalAmount());

        // ERP Extensions
        dto.setInvoiceNumber(purchase.getInvoiceNumber());
        dto.setInvoiceDate(purchase.getInvoiceDate());
        dto.setDiscountAmount(purchase.getDiscountAmount());
        dto.setGstAmount(purchase.getGstAmount());

        List<PurchaseItemDto> itemDtos = purchase.getItems().stream().map(item -> {
            PurchaseItemDto itemDto = new PurchaseItemDto();
            itemDto.setId(item.getId());
            itemDto.setProductId(item.getProduct().getId());
            itemDto.setProductName(item.getProduct().getProductName());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setPrice(item.getPrice());
            return itemDto;
        }).collect(Collectors.toList());

        dto.setItems(itemDtos);
        return dto;
    }

    private Warehouse getCurrentUserStore() {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
            .map(u -> u.getWarehouses().stream().findFirst().orElse(null))
            .orElse(null);
    }
}
