package com.medicalstore.inventory.service;

import com.medicalstore.inventory.entity.Bin;
import com.medicalstore.inventory.entity.Warehouse;
import com.medicalstore.inventory.entity.Zone;

import java.util.List;

public interface LocationService {
    // Warehouse
    Warehouse createWarehouse(Warehouse warehouse);
    Warehouse updateWarehouse(Long id, Warehouse warehouse);
    Warehouse getWarehouseById(Long id);
    List<Warehouse> getAllWarehouses();
    void deleteWarehouse(Long id);

    // Zone
    Zone createZone(Long warehouseId, Zone zone);
    Zone updateZone(Long id, Zone zone);
    Zone getZoneById(Long id);
    List<Zone> getZonesByWarehouse(Long warehouseId);
    void deleteZone(Long id);

    // Bin
    Bin createBin(Long zoneId, Bin bin);
    Bin updateBin(Long id, Bin bin);
    Bin getBinById(Long id);
    List<Bin> getBinsByZone(Long zoneId);
    void deleteBin(Long id);
}
