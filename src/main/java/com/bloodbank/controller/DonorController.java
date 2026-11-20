package com.bloodbank.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.bloodbank.model.Donor;
import com.bloodbank.service.DonorService;

@Controller
public class DonorController {

    private final DonorService donorService;

    public DonorController(DonorService donorService) {
        this.donorService = donorService;
    }

    @GetMapping("/donors")
    public String showDonors(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String keyword,
            Model model) {

        if (keyword != null && !keyword.trim().isEmpty()) {

            model.addAttribute("donors", donorService.searchDonors(keyword.trim()));

        } else {

            model.addAttribute("donors", donorService.getAllDonors());

        }

        model.addAttribute("keyword", keyword);

        return "donors";
    }


@PostMapping("/donors/register")
public String registerDonor(
        @ModelAttribute Donor donor,
        org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

    try {
        donorService.saveDonor(donor);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Donor registered successfully."
        );

    } catch (IllegalArgumentException e) {

        redirectAttributes.addFlashAttribute(
                "errorMessage",
                e.getMessage()
        );
    }

    return "redirect:/donors";
}


    @GetMapping("/donors/edit/{id}")
    public String editDonor(@PathVariable Long id, Model model) {

        Donor donor = donorService.getDonorById(id);

        model.addAttribute("donor", donor);

        return "edit-donor";
    }


@PostMapping("/donors/update/{id}")
public String updateDonor(
        @PathVariable Long id,
        @ModelAttribute Donor donor,
        org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

    try {
        donorService.updateDonor(id, donor);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Donor details updated successfully."
        );

        return "redirect:/donors";

    } catch (IllegalArgumentException e) {

        redirectAttributes.addFlashAttribute(
                "errorMessage",
                e.getMessage()
        );

        return "redirect:/donors/edit/" + id;
    }
}


    @PostMapping("/donors/delete/{id}")
    public String deleteDonor(@PathVariable Long id) {

        donorService.deleteDonor(id);

        return "redirect:/donors";
    }
}