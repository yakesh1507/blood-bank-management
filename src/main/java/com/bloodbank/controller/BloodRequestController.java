
package com.bloodbank.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bloodbank.model.BloodRequest;
import com.bloodbank.service.BloodRequestService;

@Controller
public class BloodRequestController {

    private final BloodRequestService bloodRequestService;

    public BloodRequestController(BloodRequestService bloodRequestService) {
        this.bloodRequestService = bloodRequestService;
    }

    @GetMapping("/requests")
    public String showRequests(Model model) {
        model.addAttribute("requestList",
                bloodRequestService.getAllRequests());
        return "requests";
    }

    @PostMapping("/requests/add")
    public String addRequest(
            @ModelAttribute BloodRequest request,
            RedirectAttributes redirectAttributes) {

        try {
            request.setStatus("Pending");
            bloodRequestService.saveRequest(request);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Blood request added successfully."
            );

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return "redirect:/requests";
    }

    @PostMapping("/requests/approve/{id}")
    public String approveRequest(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            bloodRequestService.approveRequest(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Request approved and inventory updated successfully."
            );

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return "redirect:/requests";
    }

    @PostMapping("/requests/reject/{id}")
    public String rejectRequest(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            bloodRequestService.rejectRequest(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Request rejected successfully."
            );

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return "redirect:/requests";
    }
}
