package com.medicalstore.inventory.service.impl;

import com.medicalstore.inventory.dto.ProductDto;
import com.medicalstore.inventory.dto.SupplierDto;
import com.medicalstore.inventory.entity.Product;
import com.medicalstore.inventory.entity.Supplier;
import com.medicalstore.inventory.repository.ProductRepository;
import com.medicalstore.inventory.repository.SupplierRepository;
import com.medicalstore.inventory.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;


@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    @Override
    @SuppressWarnings("null")
    public ProductDto createProduct(ProductDto productDto) {
        Product product = mapToEntity(productDto);
        Product savedProduct = productRepository.save(product);
        return mapToDto(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return mapToDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("null")
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getDistinctCategories() {
        return productRepository.findDistinctCategories();
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public org.springframework.data.domain.Page<ProductDto> getPaginatedProducts(int pageNo, int pageSize, String sortField, String sortDir, String keyword, String category) {
        org.springframework.data.domain.Sort sort = sortDir.equalsIgnoreCase("asc") 
            ? org.springframework.data.domain.Sort.by(sortField).ascending() 
            : org.springframework.data.domain.Sort.by(sortField).descending();
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(pageNo - 1, pageSize, sort);
        
        org.springframework.data.domain.Page<Product> page;
        
        String safeKeyword = keyword == null ? "" : keyword.trim();
        String safeCategory = category == null ? "" : category.trim();

        if (!safeKeyword.isEmpty() || !safeCategory.isEmpty()) {
            page = productRepository.searchProductsWithCategory(safeKeyword, safeCategory, pageable);
        } else {
            page = productRepository.findAll(pageable);
        }
        
        java.util.List<ProductDto> dtoList = page.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
                
        return new org.springframework.data.domain.PageImpl<>(dtoList, pageable, page.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> searchProducts(String query) {
        List<Product> products = productRepository.searchProducts(query);
        return products.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("null")
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        Product updated = mapToEntity(productDto);
        updated.setId(id);
        return mapToDto(productRepository.save(updated));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getLowStockProducts() {
        return productRepository.findLowStockProducts().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getExpiredProducts() {
        return productRepository.findExpiredProducts(java.time.LocalDate.now()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("null")
    public void updateStock(Long id, int quantityChange) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setQuantity(product.getQuantity() + quantityChange);
        productRepository.save(product);
    }

    @Override
    public void bulkUploadProducts(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            boolean firstRow = true;
            DataFormatter formatter = new DataFormatter();
            
            for (Row row : sheet) {
                if (firstRow) {
                    firstRow = false;
                    continue; // skip header
                }
                
                Cell nameCell = row.getCell(0);
                if (nameCell == null || nameCell.getCellType() == CellType.BLANK) {
                    continue; // Skip empty rows
                }
                
                ProductDto dto = new ProductDto();
                dto.setProductName(formatter.formatCellValue(nameCell));
                
                Cell descCell = row.getCell(1);
                if (descCell != null) dto.setShortDescription(formatter.formatCellValue(descCell));
                
                Cell catCell = row.getCell(2);
                if (catCell != null) dto.setCategory(formatter.formatCellValue(catCell));
                
                dto.setPurchasePrice(getBigDecimalValue(row.getCell(3)));
                dto.setSellingPrice(getBigDecimalValue(row.getCell(4)));
                dto.setMrp(getBigDecimalValue(row.getCell(5)));
                
                Cell qtyCell = row.getCell(6);
                if (qtyCell != null && qtyCell.getCellType() == CellType.NUMERIC) dto.setQuantity((int) qtyCell.getNumericCellValue());
                
                Cell rlCell = row.getCell(7);
                if (rlCell != null && rlCell.getCellType() == CellType.NUMERIC) dto.setReorderLevel((int) rlCell.getNumericCellValue());
                
                Cell suppCell = row.getCell(8);
                if (suppCell != null && suppCell.getCellType() == CellType.NUMERIC) dto.setSupplierId((long) suppCell.getNumericCellValue());
                
                Cell batchCell = row.getCell(9);
                if (batchCell != null) dto.setBatchNumber(formatter.formatCellValue(batchCell));
                
                Cell expyCell = row.getCell(10);
                if (expyCell != null) {
                    if (expyCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(expyCell)) {
                        dto.setExpiryDate(expyCell.getLocalDateTimeCellValue().toLocalDate());
                    } else if (expyCell.getCellType() == CellType.STRING) {
                        try { dto.setExpiryDate(LocalDate.parse(expyCell.getStringCellValue().trim())); } catch(Exception e){}
                    }
                }
                
                Cell rackCell = row.getCell(11);
                if (rackCell != null) dto.setRackLocation(formatter.formatCellValue(rackCell));
                
                Cell packCell = row.getCell(12);
                if (packCell != null) dto.setPackSize(formatter.formatCellValue(packCell));
                
                dto.setGstPercentage(getBigDecimalValue(row.getCell(13)));
                dto.setDiscountPercentage(getBigDecimalValue(row.getCell(14)));
                
                Cell hsnCell = row.getCell(15);
                if (hsnCell != null) dto.setHsnCode(formatter.formatCellValue(hsnCell));
                
                Cell mfgCell = row.getCell(16);
                if (mfgCell != null) dto.setManufacturer(formatter.formatCellValue(mfgCell));
                
                Cell specCell = row.getCell(17);
                if (specCell != null) dto.setSpecifications(formatter.formatCellValue(specCell));
                
                Cell imgCell = row.getCell(18);
                if (imgCell != null) dto.setImagePath(formatter.formatCellValue(imgCell));

                try {
                    this.createProduct(dto);
                } catch (Exception e) {
                    System.err.println("Failed to insert product from row: " + row.getRowNum() + " : " + e.getMessage());
                }
            }
        }
    }

    private BigDecimal getBigDecimalValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) return BigDecimal.valueOf(cell.getNumericCellValue());
        if (cell.getCellType() == CellType.STRING) {
            try { return new BigDecimal(cell.getStringCellValue().trim()); } catch (Exception e) { return null; }
        }
        return null;
    }

    @SuppressWarnings("null")
    private Product mapToEntity(ProductDto dto) {
        Supplier supplier = null;
        if (dto.getSupplierId() != null) {
            supplier = supplierRepository.findById(dto.getSupplierId())
                    .orElse(null);
        }

        return Product.builder()
                .id(dto.getId())
                .productName(dto.getProductName())
                .shortDescription(dto.getShortDescription())
                .category(dto.getCategory())
                .purchasePrice(dto.getPurchasePrice())
                .sellingPrice(dto.getSellingPrice())
                .mrp(dto.getMrp())
                .quantity(dto.getQuantity())
                .reorderLevel(dto.getReorderLevel())
                .batchNumber(dto.getBatchNumber())
                .expiryDate(dto.getExpiryDate())
                .rackLocation(dto.getRackLocation())
                .packSize(dto.getPackSize())
                .gstPercentage(dto.getGstPercentage())
                .discountPercentage(dto.getDiscountPercentage())
                .hsnCode(dto.getHsnCode())
                .manufacturer(dto.getManufacturer())
                .specifications(dto.getSpecifications())
                .imagePath(dto.getImagePath())
                .supplier(supplier)
                .build();
    }

    private ProductDto mapToDto(Product product) {
        Long supplierId = null;
        String supplierName = null;
        SupplierDto supplierDto = null;

        if (product.getSupplier() != null) {
            supplierId = product.getSupplier().getId();
            supplierName = product.getSupplier().getSupplierName();
            
            supplierDto = SupplierDto.builder()
                    .id(product.getSupplier().getId())
                    .supplierName(product.getSupplier().getSupplierName())
                    .contactPerson(product.getSupplier().getContactPerson())
                    .phone(product.getSupplier().getPhone())
                    .email(product.getSupplier().getEmail())
                    .address(product.getSupplier().getAddress())
                    .gstin(product.getSupplier().getGstin())
                    .dlNumber(product.getSupplier().getDlNumber())
                    .bankName(product.getSupplier().getBankName())
                    .accountNumber(product.getSupplier().getAccountNumber())
                    .state(product.getSupplier().getState())
                    .build();
        }

        return ProductDto.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .shortDescription(product.getShortDescription())
                .category(product.getCategory())
                .purchasePrice(product.getPurchasePrice())
                .sellingPrice(product.getSellingPrice())
                .mrp(product.getMrp())
                .quantity(product.getQuantity())
                .reorderLevel(product.getReorderLevel())
                .batchNumber(product.getBatchNumber())
                .expiryDate(product.getExpiryDate())
                .rackLocation(product.getRackLocation())
                .packSize(product.getPackSize())
                .gstPercentage(product.getGstPercentage())
                .discountPercentage(product.getDiscountPercentage())
                .hsnCode(product.getHsnCode())
                .manufacturer(product.getManufacturer())
                .specifications(product.getSpecifications())
                .imagePath(product.getImagePath())
                .supplierId(supplierId)
                .supplierName(supplierName)
                .supplier(supplierDto)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
