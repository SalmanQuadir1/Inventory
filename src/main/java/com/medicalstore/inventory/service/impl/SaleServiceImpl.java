package com.medicalstore.inventory.service.impl;

import com.medicalstore.inventory.dto.SaleDto;
import com.medicalstore.inventory.dto.SaleItemDto;
import com.medicalstore.inventory.entity.Product;
import com.medicalstore.inventory.entity.Sale;
import com.medicalstore.inventory.entity.SaleItem;
import com.medicalstore.inventory.exception.ResourceNotFoundException;
import com.medicalstore.inventory.repository.ProductRepository;
import com.medicalstore.inventory.repository.SaleRepository;
import com.medicalstore.inventory.repository.UserRepository;
import com.medicalstore.inventory.entity.User;
import com.medicalstore.inventory.entity.Warehouse;
import com.medicalstore.inventory.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@org.springframework.transaction.annotation.Transactional
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final com.medicalstore.inventory.service.StockService stockService;
    private final UserRepository userRepository;
    private final com.medicalstore.inventory.repository.CustomerRepository customerRepository;
    private final com.medicalstore.inventory.service.CustomerService customerService;

    @Override
    @Transactional
    @SuppressWarnings("null")
    public SaleDto createSale(SaleDto dto) {
        Sale sale = new Sale();
        sale.setSaleDate(dto.getSaleDate() != null ? dto.getSaleDate() : LocalDateTime.now());
        sale.setPaymentMethod(dto.getPaymentMethod());

        if ("CREDIT".equalsIgnoreCase(dto.getPaymentMethod()) && dto.getCustomerId() == null) {
            throw new RuntimeException("A registered customer must be selected for Khata/Credit sales.");
        }

        // ERP Extensions
        sale.setCustomerName(dto.getCustomerName());
        sale.setPhoneNumber(dto.getPhoneNumber());
        sale.setRemarks(dto.getRemarks());
        sale.setDiscountAmount(dto.getDiscountAmount());
        sale.setGstAmount(dto.getGstAmount());

        Warehouse store = getCurrentUserStore();
        sale.setStore(store);

        if (dto.getCustomerId() != null) {
            com.medicalstore.inventory.entity.Customer customer = customerRepository.findById(dto.getCustomerId()).orElse(null);
            sale.setCustomer(customer);
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (SaleItemDto itemDto : dto.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            int currentStock = product.getQuantity() != null ? product.getQuantity() : 0;
            if (currentStock < itemDto.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getProductName());
            }

            SaleItem item = new SaleItem();
            item.setProduct(product);
            item.setQuantity(itemDto.getQuantity());

            BigDecimal prodPrice = product.getSellingPrice() != null ? product.getSellingPrice() : BigDecimal.ZERO;
            BigDecimal priceToUse = itemDto.getPrice() != null ? itemDto.getPrice() : prodPrice;
            item.setPrice(priceToUse);

            sale.addItem(item);

            totalAmount = totalAmount.add(priceToUse.multiply(new BigDecimal(itemDto.getQuantity())));

            // Deduct from WMS inventory in local branch
            String reference = "SO-SYS-" + System.currentTimeMillis();
            Long storeId = store != null ? store.getId() : null;
            stockService.deductStockAuto(itemDto.getProductId(), itemDto.getQuantity(), reference, storeId);

            // Deduct from top level quantity for legacy support
            product.setQuantity(currentStock - itemDto.getQuantity());
            productRepository.save(product);
        }

        sale.setTotalAmount(totalAmount);
        Sale savedSale = saleRepository.save(sale);

        if ("CREDIT".equalsIgnoreCase(savedSale.getPaymentMethod()) && savedSale.getCustomer() != null) {
            customerService.recordCreditSale(savedSale.getCustomer().getId(), savedSale.getTotalAmount(), "Bill #" + savedSale.getId());
        }

        return mapToDto(savedSale);
    }

    @Override
    @SuppressWarnings("null")
    public SaleDto getSaleById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found"));
        return mapToDto(sale);
    }

    @Override
    public List<SaleDto> getAllSales() {
        return saleRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("null")
    public org.springframework.data.domain.Page<SaleDto> getPaginatedSales(int pageNo, int pageSize, String sortField, String sortDir) {
        org.springframework.data.domain.Sort sort = sortDir.equalsIgnoreCase("asc") ? 
            org.springframework.data.domain.Sort.by(sortField).ascending() : 
            org.springframework.data.domain.Sort.by(sortField).descending();
            
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(pageNo - 1, pageSize, sort);
        
        org.springframework.data.domain.Page<Sale> page = saleRepository.findAll(pageable);
        
        List<SaleDto> dtoList = page.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
                
        return new org.springframework.data.domain.PageImpl<>(dtoList, pageable, page.getTotalElements());
    }

    private SaleDto mapToDto(Sale sale) {
        SaleDto dto = new SaleDto();
        dto.setId(sale.getId());
        dto.setSaleDate(sale.getSaleDate());
        dto.setTotalAmount(sale.getTotalAmount());
        dto.setPaymentMethod(sale.getPaymentMethod());

        // ERP Extensions
        if (sale.getCustomer() != null) {
            dto.setCustomerId(sale.getCustomer().getId());
            dto.setCustomerName(sale.getCustomer().getName());
            dto.setPhoneNumber(sale.getCustomer().getPhone());
        } else {
            dto.setCustomerName(sale.getCustomerName());
            dto.setPhoneNumber(sale.getPhoneNumber());
        }
        dto.setRemarks(sale.getRemarks());
        dto.setDiscountAmount(sale.getDiscountAmount());
        dto.setGstAmount(sale.getGstAmount());

        List<SaleItemDto> itemDtos = sale.getItems().stream().map(item -> {
            SaleItemDto itemDto = new SaleItemDto();
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
            .map(User::getStore)
            .orElse(null);
    }
}
