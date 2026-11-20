
package com.bloodbank.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.bloodbank.model.BloodInventory;
import com.bloodbank.service.BloodInventoryService;

@Controller
public class BloodInventoryController {

    private final BloodInventoryService bloodInventoryService;

    public BloodInventoryController(
            BloodInventoryService bloodInventoryService) {
        this.bloodInventoryService = bloodInventoryService;
    }

    @GetMapping("/inventory")
    public String showInventory(Model model) {

        model.addAttribute("inventoryList",
                bloodInventoryService.getAllInventory());

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
        model.addAttribute("totalUnits",
                bloodInventoryService.getTotalUnits());
        model.addAttribute("today", java.time.LocalDate.now());

        return "inventory";
    }

    @PostMapping("/inventory/add")
    public String addInventory(
            @ModelAttribute BloodInventory inventory) {

        bloodInventoryService.saveInventory(inventory);
        return "redirect:/inventory";
    }

    @GetMapping("/inventory/edit/{id}")
    public String editInventory(
            @PathVariable Long id, Model model) {

        model.addAttribute("inventory",
                bloodInventoryService.getInventoryById(id));

        return "edit-inventory";
    }

    @PostMapping("/inventory/update/{id}")
    public String updateInventory(
            @PathVariable Long id,
            @ModelAttribute BloodInventory inventory) {

        bloodInventoryService.updateInventory(id, inventory);
        return "redirect:/inventory";
    }

    @PostMapping("/inventory/delete/{id}")
    public String deleteInventory(@PathVariable Long id) {

        bloodInventoryService.deleteInventory(id);
        return "redirect:/inventory";
    }
}
