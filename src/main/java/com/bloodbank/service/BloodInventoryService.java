
package com.bloodbank.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bloodbank.model.BloodInventory;
import com.bloodbank.repository.BloodInventoryRepository;

@Service
public class BloodInventoryService {

    private final BloodInventoryRepository bloodInventoryRepository;

    public BloodInventoryService(
            BloodInventoryRepository bloodInventoryRepository) {
        this.bloodInventoryRepository = bloodInventoryRepository;
    }

    public BloodInventory saveInventory(BloodInventory inventory) {
        validateInventory(inventory);
        return bloodInventoryRepository.save(inventory);
    }

    public BloodInventory getInventoryById(Long id) {
        return bloodInventoryRepository.findById(id)
                .orElseThrow(() ->
                    new IllegalArgumentException("Inventory record not found."));
    }

    public BloodInventory updateInventory(
            Long id, BloodInventory updatedInventory) {

        BloodInventory existing = getInventoryById(id);

        existing.setBloodGroup(updatedInventory.getBloodGroup());
        existing.setUnitsAvailable(updatedInventory.getUnitsAvailable());
        existing.setCollectionDate(updatedInventory.getCollectionDate());
        existing.setExpiryDate(updatedInventory.getExpiryDate());

        validateInventory(existing);

        return bloodInventoryRepository.save(existing);
    }

    public void deleteInventory(Long id) {
        BloodInventory existing = getInventoryById(id);
        bloodInventoryRepository.delete(existing);
    }

    private void validateInventory(BloodInventory inventory) {

        if (inventory.getBloodGroup() == null ||
                inventory.getBloodGroup().isBlank()) {
            throw new IllegalArgumentException(
                    "Please select a blood group.");
        }

        if (inventory.getUnitsAvailable() <= 0) {
            throw new IllegalArgumentException(
                    "Units must be greater than zero.");
        }

        if (inventory.getCollectionDate() == null ||
                inventory.getExpiryDate() == null) {
            throw new IllegalArgumentException(
                    "Collection date and expiry date are required.");
        }

        if (inventory.getExpiryDate().isBefore(
                inventory.getCollectionDate())) {
            throw new IllegalArgumentException(
                    "Expiry date cannot be before collection date.");
        }
    }

    public List<BloodInventory> getAllInventory() {
        return bloodInventoryRepository.findAll();
    }

    public List<Object[]> getBloodGroupSummary() {
        return bloodInventoryRepository.getBloodGroupSummary();
    }

    public long getTotalUnits() {
        return bloodInventoryRepository.getBloodGroupSummary()
                .stream()
                .mapToLong(row -> ((Number) row[1]).longValue())
                .sum();
    }
}
