package com.bloodbank.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bloodbank.model.Donation;
import com.bloodbank.model.Donor;
import com.bloodbank.model.BloodInventory;

import com.bloodbank.repository.DonationRepository;

@Service
public class DonationService {

    private final DonationRepository donationRepository;
    private final DonorService donorService;
    private final BloodInventoryService bloodInventoryService;

    public DonationService(
            DonationRepository donationRepository,
            DonorService donorService,
            BloodInventoryService bloodInventoryService) {

        this.donationRepository = donationRepository;
        this.donorService = donorService;
        this.bloodInventoryService = bloodInventoryService;
    }

    public List<Donation> getAllDonations() {
        return donationRepository.findAll();
    }

    @Transactional
    public Donation saveDonation(Long donorId, int units,
                                 java.time.LocalDate donationDate,
                                 java.time.LocalDate expiryDate) {

        if (units <= 0) {
            throw new IllegalArgumentException("Units must be greater than zero.");
        }

        if (donationDate == null || expiryDate == null) {
            throw new IllegalArgumentException("Donation and expiry dates are required.");
        }

        if (donationDate.isAfter(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("Donation date cannot be in the future.");
        }

        if (expiryDate.isBefore(donationDate)) {
            throw new IllegalArgumentException("Expiry date cannot be before donation date.");
        }

        Donor donor = donorService.getDonorById(donorId);

        if (donor == null) {
            throw new IllegalArgumentException("Selected donor does not exist.");
        }

        Donation donation = new Donation();

        donation.setDonorId(donor.getDonorId());
        donation.setDonorName(donor.getFullName());
        donation.setBloodGroup(donor.getBloodGroup());
        donation.setUnitsDonated(units);
        donation.setDonationDate(donationDate);
        donation.setExpiryDate(expiryDate);

        Donation savedDonation = donationRepository.save(donation);

        donor.setLastDonationDate(donationDate);
        donorService.updateDonor(donor);

        BloodInventory inventory = new BloodInventory();

        inventory.setBloodGroup(donor.getBloodGroup());
        inventory.setUnitsAvailable(units);
        inventory.setCollectionDate(donationDate);
        inventory.setExpiryDate(expiryDate);

        bloodInventoryService.saveInventory(inventory);

        return savedDonation;
    }
}