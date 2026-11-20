package com.bloodbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bloodbank.model.Donation;

public interface DonationRepository extends JpaRepository<Donation, Long> {

}