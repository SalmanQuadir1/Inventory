package com.medicalstore.inventory.repository;

import com.medicalstore.inventory.entity.Bin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BinRepository extends JpaRepository<Bin, Long> {
    List<Bin> findByZoneId(Long zoneId);
    Optional<Bin> findByName(String name);
    Optional<Bin> findByBarcode(String barcode);
}
