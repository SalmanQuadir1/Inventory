package com.medicalstore.inventory.controller;

import com.medicalstore.inventory.dto.ProductDto;
import com.medicalstore.inventory.service.FileStorageService;
import com.medicalstore.inventory.service.ProductService;
import com.medicalstore.inventory.service.SupplierService;
import com.medicalstore.inventory.service.StockService;
import com.medicalstore.inventory.repository.UserRepository;
import com.medicalstore.inventory.entity.Warehouse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import jakarta.servlet.http.HttpServletResponse;
import java.io.OutputStream;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final SupplierService supplierService;
    private final FileStorageService fileStorageService;
    private final StockService stockService;
    private final UserRepository userRepository;

    @GetMapping
    public String listProducts(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "page", defaultValue = "1") int pageNo,
            @RequestParam(value = "size", defaultValue = "10") int pageSize,
            @RequestParam(value = "sortField", defaultValue = "productName") String sortField,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            Model model) {
        
        java.util.Collection<Long> warehouseIds = getCurrentUserWarehouseIds();
        org.springframework.data.domain.Page<ProductDto> page = productService.getPaginatedProducts(pageNo, pageSize, sortField, sortDir, keyword, category, warehouseIds);
        
        model.addAttribute("products", page.getContent());
        model.addAttribute("categories", productService.getDistinctCategories());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("keyword", keyword);
        
        return "products/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("productDto", new ProductDto());
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        return "products/form";
    }

    @PostMapping("/save")
    public String saveProduct(@Valid @ModelAttribute("productDto") ProductDto productDto,
            BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            System.err.println("Validation errors: " + result.getAllErrors());
            model.addAttribute("suppliers", supplierService.getAllSuppliers());
            return "products/form";
        }

        try {
            System.out.println("Saving product: " + productDto.getProductName());

            // Handle image upload
            if (productDto.getImageFile() != null && !productDto.getImageFile().isEmpty()) {
                String fileName = fileStorageService.storeFile(productDto.getImageFile());
                productDto.setImagePath("/uploads/products/" + fileName);
                System.out.println("New image stored: " + fileName);
            } else if (productDto.getId() != null) {
                // If updating and no new image, preserve the existing one
                ProductDto existing = productService.getProductById(productDto.getId());
                productDto.setImagePath(existing.getImagePath());
                System.out.println("Preserving existing image path: " + existing.getImagePath());
            }

            if (productDto.getId() == null) {
                productService.createProduct(productDto);
                redirectAttributes.addFlashAttribute("successMessage", "Product added successfully!");
            } else {
                productService.updateProduct(productDto.getId(), productDto);
                redirectAttributes.addFlashAttribute("successMessage", "Product updated successfully!");
            }
        } catch (Exception e) {
            System.err.println("Error saving product: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("suppliers", supplierService.getAllSuppliers());
            return "products/form";
        }

        return "redirect:/products";
    }

    @GetMapping("/view/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        return "products/view";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("productDto", productService.getProductById(id));
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        return "products/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/bulk-upload/template")
    public void downloadTemplate(HttpServletResponse response) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Products");
            Row header = sheet.createRow(0);
            String[] headers = {
                "ProductName*", "ShortDescription", "Category", "PurchasePrice", 
                "SellingPrice*", "MRP", "Quantity", "ReorderLevel", "SupplierId",
                "BatchNumber", "ExpiryDate (YYYY-MM-DD)", "RackLocation", "PackSize", 
                "GstPercentage", "DiscountPercentage", "HSNCode", "Manufacturer", "Specifications"
            };
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
            }
            
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=product_upload_template.xlsx");
            
            try (OutputStream os = response.getOutputStream()) {
                workbook.write(os);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/bulk-upload")
    public String bulkUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a file to upload.");
            return "redirect:/products";
        }
        
        try {
            productService.bulkUploadProducts(file);
            redirectAttributes.addFlashAttribute("successMessage", "Products uploaded successfully from Excel.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload products: " + e.getMessage());
        }
        
        return "redirect:/products";
    }

    @GetMapping("/api/search")
    @ResponseBody
    public java.util.List<ProductDto> searchApi(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        
        java.util.Collection<Long> warehouseIds = getCurrentUserWarehouseIds();
        java.util.List<ProductDto> products = productService.getPaginatedProducts(1, size, "productName", "asc", keyword, category, warehouseIds).getContent();
        Warehouse store = getCurrentUserStore();
        
        if (store != null) {
            java.util.List<ProductDto> localProducts = new java.util.ArrayList<>();
            for (ProductDto p : products) {
                int localQty = stockService.getStockByProduct(p.getId()).stream()
                    .filter(s -> s.getBin() != null && s.getBin().getZone().getWarehouse().getId().equals(store.getId()))
                    .mapToInt(com.medicalstore.inventory.entity.Stock::getQuantity).sum();
                p.setQuantity(localQty);
                if (localQty > 0) {
                    localProducts.add(p);
                } else if (p.getQuantity() != null && p.getQuantity() > 0) {
                    // Fallback to display legacy products that have no WMS stock
                    localProducts.add(p);
                }
            }
            return localProducts;
        }
        
        return products.stream().filter(p -> p.getQuantity() != null && p.getQuantity() > 0).collect(java.util.stream.Collectors.toList());
    }

    private java.util.Collection<Long> getCurrentUserWarehouseIds() {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
            .map(u -> u.getWarehouses().stream().map(Warehouse::getId).collect(java.util.stream.Collectors.toSet()))
            .orElse(java.util.Collections.emptySet());
    }

    private Warehouse getCurrentUserStore() {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
            .map(u -> u.getWarehouses().stream().findFirst().orElse(null))
            .orElse(null);
    }
}
