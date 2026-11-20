
package com.bloodbank.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bloodbank.model.Donor;
import com.bloodbank.repository.DonorRepository;

@Service
public class DonorService {

    private final DonorRepository donorRepository;

    public DonorService(DonorRepository donorRepository) {
        this.donorRepository = donorRepository;
    }


public Donor saveDonor(Donor donor) {

    if (donor.getFullName() == null ||
            donor.getFullName().isBlank()) {
        throw new IllegalArgumentException("Donor name is required.");
    }

    if (donor.getAge() < 18 || donor.getAge() > 65) {
        throw new IllegalArgumentException("Donor age must be between 18 and 65.");
    }

    if (donor.getBloodGroup() == null ||
            donor.getBloodGroup().isBlank()) {
        throw new IllegalArgumentException("Please select a blood group.");
    }

    if (donor.getPhoneNumber() == null ||
            !donor.getPhoneNumber().matches("[0-9]{10}")) {
        throw new IllegalArgumentException("Enter a valid 10-digit phone number.");
    }

    if (donor.getEmail() == null ||
            !donor.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
        throw new IllegalArgumentException("Enter a valid email address.");
    }

    return donorRepository.save(donor);
}


    public List<Donor> getAllDonors() {
        return donorRepository.findAll();
    }

    public Donor getDonorById(Long donorId) {
        return donorRepository.findById(donorId).orElse(null);
    }


public Donor updateDonor(Long donorId, Donor updatedDonor) {

    Donor existingDonor = donorRepository.findById(donorId)
            .orElseThrow(() ->
                    new IllegalArgumentException("Donor not found."));

    updatedDonor.setDonorId(existingDonor.getDonorId());

    return saveDonor(updatedDonor);
}


    public Donor updateDonor(Donor donor) {
        return donorRepository.save(donor);
    }

    public void deleteDonor(Long donorId) {
        donorRepository.deleteById(donorId);
    }

    public List<Donor> searchDonors(String keyword) {

        return donorRepository
                .findByFullNameContainingIgnoreCaseOrBloodGroupContainingIgnoreCaseOrCityContainingIgnoreCase(
                        keyword, keyword, keyword
                );
    }
}
