
package com.bloodbank.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bloodbank.model.BloodRequest;
import com.bloodbank.model.BloodInventory;
import com.bloodbank.repository.BloodRequestRepository;

@Service
public class BloodRequestService {

    private final BloodRequestRepository bloodRequestRepository;
    private final BloodInventoryService bloodInventoryService;

    public BloodRequestService(
            BloodRequestRepository bloodRequestRepository,
            BloodInventoryService bloodInventoryService) {

        this.bloodRequestRepository = bloodRequestRepository;
        this.bloodInventoryService = bloodInventoryService;
    }

    public BloodRequest saveRequest(BloodRequest request) {

        if (request.getHospitalName() == null ||
                request.getHospitalName().isBlank()) {
            throw new IllegalArgumentException("Hospital name is required.");
        }

        if (request.getContactPerson() == null ||
                request.getContactPerson().isBlank()) {
            throw new IllegalArgumentException("Contact person is required.");
        }

        if (request.getPhoneNumber() == null ||
                !request.getPhoneNumber().matches("[0-9]{10}")) {
            throw new IllegalArgumentException("Enter a valid 10-digit phone number.");
        }

        if (request.getBloodGroup() == null ||
                request.getBloodGroup().isBlank()) {
            throw new IllegalArgumentException("Please select a blood group.");
        }

        if (request.getUnitsRequired() <= 0) {
            throw new IllegalArgumentException("Units required must be greater than zero.");
        }

        if (request.getRequestDate() == null ||
                request.getRequiredByDate() == null) {
            throw new IllegalArgumentException("Request dates are required.");
        }

        if (request.getRequiredByDate().isBefore(request.getRequestDate())) {
            throw new IllegalArgumentException("Required date cannot be before request date.");
        }

        return bloodRequestRepository.save(request);
    }

    public List<BloodRequest> getAllRequests() {
        return bloodRequestRepository.findAll();
    }

    public BloodRequest getRequestById(Long id) {
        return bloodRequestRepository.findById(id).orElse(null);
    }

    @Transactional
    public BloodRequest approveRequest(Long id) {

        BloodRequest request = getRequestById(id);

        if (request == null) {
            throw new IllegalArgumentException("Blood request not found.");
        }

        if (!"Pending".equalsIgnoreCase(request.getStatus())) {
            throw new IllegalArgumentException("Only pending requests can be approved.");
        }

        LocalDate today = LocalDate.now();

        List<BloodInventory> availableBatches =
                bloodInventoryService.getAllInventory()
                        .stream()
                        .filter(item ->
                                item.getBloodGroup().equalsIgnoreCase(
                                        request.getBloodGroup()))
                        .filter(item ->
                                item.getExpiryDate() != null &&
                                !item.getExpiryDate().isBefore(today))
                        .filter(item -> item.getUnitsAvailable() > 0)
                        .sorted(Comparator.comparing(
                                BloodInventory::getExpiryDate))
                        .toList();

        int availableUnits = availableBatches.stream()
                .mapToInt(BloodInventory::getUnitsAvailable)
                .sum();

        if (availableUnits < request.getUnitsRequired()) {
            throw new IllegalArgumentException(
                    "Insufficient " + request.getBloodGroup()
                    + " blood. Available units: " + availableUnits);
        }

        int remainingUnits = request.getUnitsRequired();

        for (BloodInventory item : availableBatches) {

            if (remainingUnits <= 0) {
                break;
            }

            int currentUnits = item.getUnitsAvailable();

            if (currentUnits <= remainingUnits) {
                remainingUnits -= currentUnits;
                bloodInventoryService.deleteInventory(
                        item.getInventoryId());
            } else {
                item.setUnitsAvailable(currentUnits - remainingUnits);
                bloodInventoryService.saveInventory(item);
                remainingUnits = 0;
            }
        }

        request.setStatus("Approved");

        return bloodRequestRepository.save(request);
    }

    public BloodRequest rejectRequest(Long id) {

        BloodRequest request = getRequestById(id);

        if (request == null) {
            throw new IllegalArgumentException("Blood request not found.");
        }

        if (!"Pending".equalsIgnoreCase(request.getStatus())) {
            throw new IllegalArgumentException("Only pending requests can be rejected.");
        }

        request.setStatus("Rejected");

        return bloodRequestRepository.save(request);
    }

    public void deleteRequest(Long id) {
        bloodRequestRepository.deleteById(id);
    }
}
