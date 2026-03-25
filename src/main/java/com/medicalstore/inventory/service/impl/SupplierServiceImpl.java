package com.medicalstore.inventory.service.impl;

import com.medicalstore.inventory.dto.SupplierDto;
import com.medicalstore.inventory.entity.Supplier;
import com.medicalstore.inventory.exception.ResourceNotFoundException;
import com.medicalstore.inventory.repository.SupplierRepository;
import com.medicalstore.inventory.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@org.springframework.transaction.annotation.Transactional
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    @Override
    @SuppressWarnings("null")
    public SupplierDto createSupplier(SupplierDto supplierDto) {
        Supplier supplier = mapToEntity(supplierDto);
        Supplier savedSupplier = supplierRepository.save(supplier);
        return mapToDto(savedSupplier);
    }

    @Override
    @SuppressWarnings("null")
    public SupplierDto updateSupplier(Long id, SupplierDto supplierDto) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));

        supplier.setSupplierName(supplierDto.getSupplierName());
        supplier.setContactPerson(supplierDto.getContactPerson());
        supplier.setPhone(supplierDto.getPhone());
        supplier.setEmail(supplierDto.getEmail());
        supplier.setAddress(supplierDto.getAddress());

        // Marg ERP Extensions
        supplier.setGstin(supplierDto.getGstin());
        supplier.setDlNumber(supplierDto.getDlNumber());
        supplier.setBankName(supplierDto.getBankName());
        supplier.setAccountNumber(supplierDto.getAccountNumber());
        supplier.setIfscCode(supplierDto.getIfscCode());
        supplier.setState(supplierDto.getState());

        Supplier updatedSupplier = supplierRepository.save(supplier);
        return mapToDto(updatedSupplier);
    }

    @Override
    @SuppressWarnings("null")
    public void deleteSupplier(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new ResourceNotFoundException("Supplier not found");
        }

        if (supplierRepository.isUsedInProducts(id)) {
            throw new IllegalStateException(
                    "Cannot delete supplier: There are products linked to this supplier. Please update or delete those products first.");
        }

        if (supplierRepository.isUsedInPurchases(id)) {
            throw new IllegalStateException(
                    "Cannot delete supplier: There are purchases linked to this supplier. Please delete associated purchases first.");
        }

        supplierRepository.deleteById(id);
    }

    @Override
    @SuppressWarnings("null")
    public SupplierDto getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
        return mapToDto(supplier);
    }

    @Override
    public List<SupplierDto> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("null")
    public org.springframework.data.domain.Page<SupplierDto> getPaginatedSuppliers(int pageNo, int pageSize, String sortField, String sortDir) {
        org.springframework.data.domain.Sort sort = sortDir.equalsIgnoreCase("asc") ? 
            org.springframework.data.domain.Sort.by(sortField).ascending() : 
            org.springframework.data.domain.Sort.by(sortField).descending();
            
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(pageNo - 1, pageSize, sort);
        
        org.springframework.data.domain.Page<Supplier> page = supplierRepository.findAll(pageable);
        
        List<SupplierDto> dtoList = page.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
                
        return new org.springframework.data.domain.PageImpl<>(dtoList, pageable, page.getTotalElements());
    }

    private SupplierDto mapToDto(Supplier supplier) {
        SupplierDto dto = new SupplierDto();
        dto.setId(supplier.getId());
        dto.setSupplierName(supplier.getSupplierName());
        dto.setContactPerson(supplier.getContactPerson());
        dto.setPhone(supplier.getPhone());
        dto.setEmail(supplier.getEmail());
        dto.setAddress(supplier.getAddress());

        // Marg ERP Extensions
        dto.setGstin(supplier.getGstin());
        dto.setDlNumber(supplier.getDlNumber());
        dto.setBankName(supplier.getBankName());
        dto.setAccountNumber(supplier.getAccountNumber());
        dto.setIfscCode(supplier.getIfscCode());
        dto.setState(supplier.getState());

        return dto;
    }

    private Supplier mapToEntity(SupplierDto dto) {
        Supplier supplier = new Supplier();
        supplier.setSupplierName(dto.getSupplierName());
        supplier.setContactPerson(dto.getContactPerson());
        supplier.setPhone(dto.getPhone());
        supplier.setEmail(dto.getEmail());
        supplier.setAddress(dto.getAddress());

        // Marg ERP Extensions
        supplier.setGstin(dto.getGstin());
        supplier.setDlNumber(dto.getDlNumber());
        supplier.setBankName(dto.getBankName());
        supplier.setAccountNumber(dto.getAccountNumber());
        supplier.setIfscCode(dto.getIfscCode());
        supplier.setState(dto.getState());

        return supplier;
    }
}
