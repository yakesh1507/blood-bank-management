
package com.bloodbank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bloodbank.model.BloodInventory;

public interface BloodInventoryRepository extends JpaRepository<BloodInventory, Long> {

    @Query("SELECT b.bloodGroup, SUM(b.unitsAvailable) " +
           "FROM BloodInventory b " +
           "WHERE b.expiryDate >= CURRENT_DATE " +
           "GROUP BY b.bloodGroup")
    List<Object[]> getBloodGroupSummary();

}
