
package com.bloodbank.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.bloodbank.model.Donor;
import com.bloodbank.model.BloodInventory;
import com.bloodbank.model.BloodRequest;
import com.bloodbank.model.Donation;

import com.bloodbank.service.DonorService;
import com.bloodbank.service.BloodInventoryService;
import com.bloodbank.service.BloodRequestService;
import com.bloodbank.service.DonationService;

@Controller
public class HomeController {

    private final DonorService donorService;
    private final BloodInventoryService bloodInventoryService;
    private final BloodRequestService bloodRequestService;
    private final DonationService donationService;

    public HomeController(
            DonorService donorService,
            BloodInventoryService bloodInventoryService,
            BloodRequestService bloodRequestService,
            DonationService donationService) {

        this.donorService = donorService;
        this.bloodInventoryService = bloodInventoryService;
        this.bloodRequestService = bloodRequestService;
        this.donationService = donationService;
    }

    @GetMapping("/")
    public String home(Model model) {

        List<Donor> donors = donorService.getAllDonors();
        List<BloodInventory> inventory =
                bloodInventoryService.getAllInventory();
        List<BloodRequest> requests =
                bloodRequestService.getAllRequests();
        List<Donation> donations =
                donationService.getAllDonations();

        model.addAttribute("totalDonors", donors.size());

        model.addAttribute("totalUnits",
                bloodInventoryService.getTotalUnits());

        model.addAttribute("pendingRequests",
                requests.stream()
                        .filter(r -> "Pending".equalsIgnoreCase(r.getStatus()))
                        .count());

        model.addAttribute("totalDonations", donations.size());

        Map<String, Long> bloodSummary = new HashMap<>();

        for (Object[] row : bloodInventoryService.getBloodGroupSummary()) {
            bloodSummary.put(
                    (String) row[0],
                    ((Number) row[1]).longValue()
            );
        }

        model.addAttribute("bloodSummary", bloodSummary);

        List<Map<String, String>> activities = new ArrayList<>();

        for (Donation donation : donations) {
            Map<String, String> activity = new HashMap<>();
            activity.put("title", "Blood Donation");
            activity.put("description",
                    donation.getDonorName() + " donated "
                    + donation.getUnitsDonated() + " units ("
                    + donation.getBloodGroup() + ")");
            activity.put("date",
                    donation.getDonationDate().toString());
            activity.put("sortDate",
                    donation.getDonationDate().toString());
            activity.put("icon", "donation");
            activities.add(activity);
        }

        for (BloodRequest request : requests) {
            Map<String, String> activity = new HashMap<>();
            activity.put("title", "Blood Request");
            activity.put("description",
                    request.getHospitalName() + " requested "
                    + request.getUnitsRequired() + " units ("
                    + request.getBloodGroup() + ") - "
                    + request.getStatus());
            activity.put("date", request.getRequestDate().toString());
            activity.put("sortDate", request.getRequestDate().toString());
            activity.put("icon", "request");
            activities.add(activity);
        }

        for (BloodInventory item : inventory) {
            Map<String, String> activity = new HashMap<>();
            activity.put("title", "Blood Inventory");
            activity.put("description",
                    item.getUnitsAvailable() + " units of "
                    + item.getBloodGroup() + " added to inventory");
            activity.put("date", item.getCollectionDate().toString());
            activity.put("sortDate", item.getCollectionDate().toString());
            activity.put("icon", "inventory");
            activities.add(activity);
        }

        for (Donor donor : donors) {
            if (donor.getLastDonationDate() != null) {
                Map<String, String> activity = new HashMap<>();
                activity.put("title", "Donor Activity");
                activity.put("description",
                        donor.getFullName() + " ("
                        + donor.getBloodGroup() + ")");
                activity.put("date",
                        donor.getLastDonationDate().toString());
                activity.put("sortDate",
                        donor.getLastDonationDate().toString());
                activity.put("icon", "donor");
                activities.add(activity);
            }
        }

        activities.sort(
                Comparator.comparing(
                        (Map<String, String> a) -> a.get("sortDate")
                ).reversed()
        );

        model.addAttribute("recentActivities",
                activities.stream().limit(5).toList());

        return "index";
    }
}
