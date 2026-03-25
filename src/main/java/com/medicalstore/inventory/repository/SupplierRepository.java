package com.medicalstore.inventory.repository;

import com.medicalstore.inventory.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.supplier.id = :id")
    boolean isUsedInProducts(@Param("id") Long id);

    @Query("SELECT COUNT(p) > 0 FROM Purchase p WHERE p.supplier.id = :id")
    boolean isUsedInPurchases(@Param("id") Long id);
}
