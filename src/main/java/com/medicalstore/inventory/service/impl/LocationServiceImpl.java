package com.medicalstore.inventory.service.impl;

import com.medicalstore.inventory.entity.Bin;
import com.medicalstore.inventory.entity.Warehouse;
import com.medicalstore.inventory.entity.Zone;
import com.medicalstore.inventory.repository.BinRepository;
import com.medicalstore.inventory.repository.WarehouseRepository;
import com.medicalstore.inventory.repository.ZoneRepository;
import com.medicalstore.inventory.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LocationServiceImpl implements LocationService {

    private final WarehouseRepository warehouseRepository;
    private final ZoneRepository zoneRepository;
    private final BinRepository binRepository;

    @Override
    @SuppressWarnings("null")
    public Warehouse createWarehouse(Warehouse warehouse) {
        return warehouseRepository.save(warehouse);
    }

    @Override
    public Warehouse updateWarehouse(Long id, Warehouse details) {
        Warehouse w = getWarehouseById(id);
        w.setName(details.getName());
        w.setCode(details.getCode());
        w.setLocation(details.getLocation());
        w.setManagerName(details.getManagerName());
        w.setActive(details.isActive());
        return warehouseRepository.save(w);
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public Warehouse getWarehouseById(Long id) {
        return warehouseRepository.findById(id).orElseThrow(() -> new RuntimeException("Warehouse not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    @Override
    @SuppressWarnings("null")
    public void deleteWarehouse(Long id) {
        warehouseRepository.deleteById(id);
    }

    @Override
    public Zone createZone(Long warehouseId, Zone zone) {
        Warehouse w = getWarehouseById(warehouseId);
        zone.setWarehouse(w);
        return zoneRepository.save(zone);
    }

    @Override
    public Zone updateZone(Long id, Zone details) {
        Zone z = getZoneById(id);
        z.setName(details.getName());
        z.setCode(details.getCode());
        z.setType(details.getType());
        return zoneRepository.save(z);
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public Zone getZoneById(Long id) {
        return zoneRepository.findById(id).orElseThrow(() -> new RuntimeException("Zone not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Zone> getZonesByWarehouse(Long warehouseId) {
        return zoneRepository.findByWarehouseId(warehouseId);
    }

    @Override
    @SuppressWarnings("null")
    public void deleteZone(Long id) {
        zoneRepository.deleteById(id);
    }

    @Override
    public Bin createBin(Long zoneId, Bin bin) {
        Zone z = getZoneById(zoneId);
        bin.setZone(z);
        if (bin.getBarcode() == null || bin.getBarcode().isBlank()) {
            bin.setBarcode(bin.getName());
        }
        return binRepository.save(bin);
    }

    @Override
    public Bin updateBin(Long id, Bin details) {
        Bin b = getBinById(id);
        b.setName(details.getName());
        b.setBarcode(details.getBarcode());
        b.setWeightCapacity(details.getWeightCapacity());
        b.setDimensions(details.getDimensions());
        b.setActive(details.isActive());
        return binRepository.save(b);
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public Bin getBinById(Long id) {
        return binRepository.findById(id).orElseThrow(() -> new RuntimeException("Bin not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Bin> getBinsByZone(Long zoneId) {
        return binRepository.findByZoneId(zoneId);
    }

    @Override
    @SuppressWarnings("null")
    public void deleteBin(Long id) {
        binRepository.deleteById(id);
    }
}
