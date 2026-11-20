package com.bloodbank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bloodbank.model.Donor;

public interface DonorRepository extends JpaRepository<Donor, Long> {

    List<Donor> findByFullNameContainingIgnoreCaseOrBloodGroupContainingIgnoreCaseOrCityContainingIgnoreCase(
            String fullName,
            String bloodGroup,
            String city
    );

}