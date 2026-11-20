package com.bloodbank.controller;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bloodbank.service.DonationService;
import com.bloodbank.service.DonorService;

@Controller
public class DonationController {

    private final DonationService donationService;
    private final DonorService donorService;

    public DonationController(
            DonationService donationService,
            DonorService donorService) {

        this.donationService = donationService;
        this.donorService = donorService;
    }

    @GetMapping("/donations")
    public String showDonations(Model model) {

        model.addAttribute("donationList",
                donationService.getAllDonations());

        model.addAttribute("donors",
                donorService.getAllDonors());

        model.addAttribute("today", LocalDate.now());

        return "donations";
    }

    @PostMapping("/donations/add")
    public String addDonation(
            @RequestParam Long donorId,
            @RequestParam int unitsDonated,
            @RequestParam LocalDate donationDate,
            @RequestParam LocalDate expiryDate,
            RedirectAttributes redirectAttributes) {

        try {

            donationService.saveDonation(
                    donorId,
                    unitsDonated,
                    donationDate,
                    expiryDate
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Donation recorded and inventory updated successfully."
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return "redirect:/donations";
    }
}