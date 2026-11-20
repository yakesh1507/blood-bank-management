
package com.bloodbank.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.bloodbank.service.DonorService;
import com.bloodbank.service.BloodInventoryService;
import com.bloodbank.service.BloodRequestService;
import com.bloodbank.service.DonationService;

@Controller
public class ReportController {

    private final DonorService donorService;
    private final BloodInventoryService bloodInventoryService;
    private final BloodRequestService bloodRequestService;
    private final DonationService donationService;

    public ReportController(
            DonorService donorService,
            BloodInventoryService bloodInventoryService,
            BloodRequestService bloodRequestService,
            DonationService donationService) {

        this.donorService = donorService;
        this.bloodInventoryService = bloodInventoryService;
        this.bloodRequestService = bloodRequestService;
        this.donationService = donationService;
    }

    @GetMapping("/reports")
    public String showReports(Model model) {

        model.addAttribute("totalDonors",
                donorService.getAllDonors().size());

        model.addAttribute("totalUnits",
                bloodInventoryService.getTotalUnits());

        model.addAttribute("totalRequests",
                bloodRequestService.getAllRequests().size());

        model.addAttribute("totalDonations",
                donationService.getAllDonations().size());

        long pendingRequests = bloodRequestService.getAllRequests()
                .stream()
                .filter(request ->
                        "Pending".equalsIgnoreCase(request.getStatus()))
                .count();

        model.addAttribute("pendingRequests", pendingRequests);

        List<Object[]> summary =
                bloodInventoryService.getBloodGroupSummary();

        Map<String, Long> bloodSummary = new HashMap<>();

        for (Object[] row : summary) {
            bloodSummary.put(
                    (String) row[0],
                    ((Number) row[1]).longValue()
            );
        }

        model.addAttribute("bloodSummary", bloodSummary);

        return "reports";
    }
}
