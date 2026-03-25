package com.medicalstore.inventory;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import java.io.FileOutputStream;
import java.util.Random;
import java.util.Arrays;
import java.util.List;


import org.junit.jupiter.api.Test;

public class GenerateMixWithImagesExcel {

    @Test
    public void generateExcel() throws Exception {
        // SXSSFWorkbook is a streaming wrapper around XSSFWorkbook to handle large files and prevent OOM
        SXSSFWorkbook workbook = new SXSSFWorkbook(100); 
        Sheet sheet = workbook.createSheet("Products");
        Row header = sheet.createRow(0);
        String[] headers = {
            "ProductName*", "ShortDescription", "Category", "PurchasePrice", 
            "SellingPrice*", "MRP", "Quantity", "ReorderLevel", "SupplierId",
            "BatchNumber", "ExpiryDate (YYYY-MM-DD)", "RackLocation", "PackSize", 
            "GstPercentage", "DiscountPercentage", "HSNCode", "Manufacturer", "Specifications", "ImagePath"
        };
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
        }
        
        Random rand = new Random();
        
        // Electronics Data
        List<String> elecMakers = Arrays.asList("Apple", "Samsung", "Sony", "Dell", "HP", "Lenovo", "Asus", "Acer", "LG");
        List<String> elecCats = Arrays.asList("Smartphone", "Laptop", "Tablet", "Monitor", "Audio", "Accessories");
        String[] elecNames = {"Pro Model X", "UltraBook 15", "Gaming Monitor", "Wireless Earbuds", "Smartwatch Ser 8", "Magic Keyboard", "PowerBank 20000mAh"};
        
        // Clothing Data
        List<String> clothMakers = Arrays.asList("Nike", "Adidas", "Puma", "Levi's", "Zara", "H&M", "Under Armour", "Calvin Klein");
        List<String> clothCats = Arrays.asList("T-Shirts", "Jeans", "Jackets", "Activewear", "Footwear", "Accessories");
        String[] clothNames = {"Graphic T-Shirt", "Slim Fit Jeans", "Winter Puffer Jacket", "Running Sneakers", "Cotton Hoodie", "Formal Trousers", "Sports Cap"};
        
        int totalRows = 15000; 
        
        for (int i = 1; i <= totalRows; i++) {
            Row row = sheet.createRow(i);
            
            boolean isElec = rand.nextBoolean();
            
            String mfg = isElec ? elecMakers.get(rand.nextInt(elecMakers.size())) : clothMakers.get(rand.nextInt(clothMakers.size()));
            String category = isElec ? elecCats.get(rand.nextInt(elecCats.size())) : clothCats.get(rand.nextInt(clothCats.size()));
            String baseName = isElec ? elecNames[rand.nextInt(elecNames.length)] : clothNames[rand.nextInt(clothNames.length)];
            
            String productName = mfg + " " + baseName + " " + (isElec ? (rand.nextInt(5)+1)+"TB" : "Size " + (Arrays.asList("S","M","L","XL").get(rand.nextInt(4))));
            
            row.createCell(0).setCellValue(productName); 
            row.createCell(1).setCellValue("Premium " + category.toLowerCase() + " by " + mfg); 
            row.createCell(2).setCellValue(category); 
            
            double pp = isElec ? (500 + rand.nextDouble() * 15000) : (100 + rand.nextDouble() * 3000);
            row.createCell(3).setCellValue(Math.round(pp * 100.0) / 100.0); 
            row.createCell(4).setCellValue(Math.round(pp * 1.3 * 100.0) / 100.0); 
            row.createCell(5).setCellValue(Math.round(pp * 1.5 * 100.0) / 100.0); 
            
            row.createCell(6).setCellValue(5 + rand.nextInt(200)); 
            row.createCell(7).setCellValue(10 + rand.nextInt(20)); 
            row.createCell(8).setCellValue(2); // SupplierId 2
            
            row.createCell(9).setCellValue("B" + String.format("%06d", rand.nextInt(999999))); // Batch
            
            row.createCell(10).setCellValue(""); // No expiry date!
            
            row.createCell(11).setCellValue(isElec ? "Aisle E-Rack " + (1+rand.nextInt(5)) : "Aisle C-Rack " + (1+rand.nextInt(5))); 
            row.createCell(12).setCellValue("1 Unit"); 
            
            row.createCell(13).setCellValue(isElec ? 18.0 : 5.0); // GST diff for elec vs cloth
            row.createCell(14).setCellValue(rand.nextInt(15)); // Discount
            
            row.createCell(15).setCellValue(isElec ? "8517" : "6109"); // HSN Code
            row.createCell(16).setCellValue(mfg); 
            row.createCell(17).setCellValue("Color: " + (Arrays.asList("Black","White","Blue","Red").get(rand.nextInt(4)))); 
            
            // Map the category specifically to a highly relevant static 100% guaranteed Unsplash URL
            String fixedUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=300"; // default
            
            switch (category) {
                case "Smartphone": fixedUrl = "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=300"; break;
                case "Laptop": fixedUrl = "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=300"; break;
                case "Tablet": fixedUrl = "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=300"; break;
                case "Monitor": fixedUrl = "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=300"; break;
                case "Audio": fixedUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=300"; break;
                case "T-Shirts": fixedUrl = "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=300"; break;
                case "Jeans": fixedUrl = "https://images.unsplash.com/photo-1542272604-787c3835535d?w=300"; break;
                case "Jackets": fixedUrl = "https://images.unsplash.com/photo-1551028719-00167b16eac5?w=300"; break;
                case "Activewear": fixedUrl = "https://images.unsplash.com/photo-1518459031867-a89b944bffe4?w=300"; break;
                case "Footwear": fixedUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=300"; break;
                case "Accessories": fixedUrl = isElec ? "https://images.unsplash.com/photo-1583394838336-acd977736f90?w=300" : "https://images.unsplash.com/photo-1509631179647-06773df4d471?w=300"; break;
            }

            // Assign image path guaranteed to be highly relevant
            row.createCell(18).setCellValue(fixedUrl);
        }
        
        try (FileOutputStream out = new FileOutputStream("ecommerce_15000_with_images.xlsx")) {
            workbook.write(out);
        }
        // Dispose of temporary files backing this workbook on disk
        workbook.dispose();
        workbook.close();
        System.out.println("15000 item Excel with images generated successfully!");
    }
}
