package com.manmeet.animalsys.controller;

import com.manmeet.animalsys.entity.Shelter;
import com.manmeet.animalsys.entity.User;
import com.manmeet.animalsys.service.ShelterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/shelters")
public class ShelterController {

    @Autowired
    private ShelterService shelterService;

    // Only Admin can create a new shelter (Form page)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/create")
    public String showCreateShelterForm(Model model) {
        model.addAttribute("shelter", new Shelter());
        return "shelter-create"; // HTML form for creating a shelter
    }

    // Save the new shelter
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/save")
    public String createShelter(@ModelAttribute Shelter shelter) {
        shelterService.saveShelter(shelter);
        return "redirect:/shelters";
    }

    // All users can view all shelters
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('STAFF')")
    @GetMapping
    public String getAllShelters(Model model) {
        List<Shelter> shelters = shelterService.getAllShelters();
        model.addAttribute("shelters", shelters);
        return "shelter-list"; // HTML page displaying the list of shelters
    }

    // Admin and Staff can view details of a specific shelter
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('STAFF')")
    @GetMapping("/{id}")
    public String getShelterById(@PathVariable Long id, Model model) {
        Optional<Shelter> shelter = shelterService.getShelterById(id);
        if (shelter.isPresent()) {
            model.addAttribute("shelter", shelter.get());
            return "shelter-details"; // HTML page for viewing shelter details
        } else {
            return "error-page"; // Error page if shelter not found
        }
    }

    // Only Admin can update a shelter
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{id}")
    public String showEditShelterForm(@PathVariable Long id, Model model) {
        Optional<Shelter> shelter = shelterService.getShelterById(id);
        if (shelter.isPresent()) {
            model.addAttribute("shelter", shelter.get());
            return "shelter-edit"; // HTML form for editing a shelter
        } else {
            return "error-page";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update")
    public String updateShelter(@ModelAttribute Shelter shelter) {
        shelterService.updateShelter(shelter);
        return "redirect:/shelters";
    }

    // Only Admin can delete a shelter
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/delete/{id}")
    public String deleteShelter(@PathVariable Long id) {
        shelterService.deleteShelter(id);
        return "redirect:/shelters";
    }

    // All users can search for shelters
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('STAFF')")
    @GetMapping("/search")
    public String searchShelters(@RequestParam(required = false) String location,
                                 @RequestParam(required = false) Integer capacity,
                                 Model model) {
        List<Shelter> shelters = shelterService.searchShelters(location, capacity);
        model.addAttribute("shelters", shelters);
        return "shelter-list"; // Display search results on the same shelter list page
    }

    // Only Admin and Staff can increase the capacity of a shelter
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @PostMapping("/{id}/capacity/increase")
    public String increaseCapacity(@PathVariable Long id, @RequestParam int increment) {
        shelterService.increaseCapacity(id, increment);
        return "redirect:/shelters/" + id;
    }

    // Only Admin and Staff can decrease the capacity of a shelter
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @PostMapping("/{id}/capacity/decrease")
    public String decreaseCapacity(@PathVariable Long id, @RequestParam int decrement) {
        shelterService.decreaseCapacity(id, decrement);
        return "redirect:/shelters/" + id;
    }

    // Only Admin and Staff can add staff to a shelter
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @GetMapping("/{shelterId}/staff/add")
    public String showAddStaffForm(@PathVariable Long shelterId, Model model) {
        model.addAttribute("staff", new User());
        model.addAttribute("shelterId", shelterId);
        return "shelter-staff-add"; // Form for adding staff
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @PostMapping("/{shelterId}/staff/save")
    public String addStaffToShelter(@PathVariable Long shelterId, @ModelAttribute User staff) {
        shelterService.addStaffToShelter(shelterId, staff);
        return "redirect:/shelters/" + shelterId + "/staff";
    }

    // Only Admin can remove staff from a shelter
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{shelterId}/staff/delete/{staffId}")
    public String removeStaffFromShelter(@PathVariable Long shelterId, @PathVariable Long staffId) {
        shelterService.removeStaffFromShelter(shelterId, staffId);
        return "redirect:/shelters/" + shelterId + "/staff";
    }

    // All users can view staff by shelter
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('STAFF')")
    @GetMapping("/{shelterId}/staff")
    public String getStaffByShelter(@PathVariable Long shelterId, Model model) {
        List<User> staff = shelterService.getStaffByShelter(shelterId);
        model.addAttribute("staff", staff);
        return "shelter-staff-list"; // Page for displaying staff members of a shelter
    }
}
