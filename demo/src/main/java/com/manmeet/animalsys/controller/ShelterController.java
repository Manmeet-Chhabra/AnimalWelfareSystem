package com.manmeet.animalsys.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.manmeet.animalsys.entity.Role;
import com.manmeet.animalsys.entity.Shelter;
import com.manmeet.animalsys.entity.User;
import com.manmeet.animalsys.repos.RoleRepository;
import com.manmeet.animalsys.service.ShelterService;
import com.manmeet.animalsys.service.UserService;

@Controller
@RequestMapping("/shelters")
public class ShelterController {

	@Autowired
	private ShelterService shelterService;

	@Autowired
	private UserService userService;
	
	 @Autowired
	    private RoleRepository roleRepository;

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
			model.addAttribute("error", "Shelter not found");
			return "error-page"; // Make sure error-page.html exists
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
			model.addAttribute("error", "Shelter not found");
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
			@RequestParam(required = false) Integer capacity, Model model) {
		List<Shelter> shelters = shelterService.searchShelters(location, capacity);
		model.addAttribute("shelters", shelters);
		return "shelter-list"; // Display search results on the same shelter list page
	}

	/*
	 * // Only Admin and Staff can increase the capacity of a shelter
	 * 
	 * @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	 * 
	 * @PostMapping("/{id}/capacity/increase") public String
	 * increaseCapacity(@PathVariable Long id, @RequestParam int increment) {
	 * shelterService.increaseCapacity(id, increment); return "redirect:/shelters/"
	 * + id; }
	 * 
	 * // Only Admin and Staff can decrease the capacity of a shelter
	 * 
	 * @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	 * 
	 * @PostMapping("/{id}/capacity/decrease") public String
	 * decreaseCapacity(@PathVariable Long id, @RequestParam int decrement) {
	 * shelterService.decreaseCapacity(id, decrement); return "redirect:/shelters/"
	 * + id; }
	 */

	// Only Admin and Staff can add staff to a shelter
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	@GetMapping("/{shelterId}/staff/add")
	public String showAddStaffForm(@PathVariable Long shelterId, Model model) {
		// Create a new User object for form binding
		model.addAttribute("staff", new User());

		// Add shelterId to the model
		model.addAttribute("shelterId", shelterId);

		// Fetch all users with the STAFF role
		List<User> staffMembers = userService.findByRole("ROLE_STAFF");
		model.addAttribute("staffMembers", staffMembers);

		return "shelter-staff-add"; // Form for adding staff
	}

	@PostMapping("/{shelterId}/staff/save")
	public String addStaffToShelter(@PathVariable Long shelterId, @ModelAttribute User staff, RedirectAttributes redirectAttributes) {
	    try {
	        // Check if the email is null or empty before attempting to add staff
	        if (staff.getEmail() == null || staff.getEmail().isEmpty()) {
	            redirectAttributes.addFlashAttribute("error", "Email cannot be empty.");
	            return "redirect:/shelters/" + shelterId + "/staff/add"; // Redirect back to form with error
	        }

	        // Fetch the "ROLE_STAFF" role
	        Role staffRole = roleRepository.findByName("ROLE_STAFF");
	        if (staffRole != null) {
	            staff.getRoles().add(staffRole); // Add the staff role to the user
	        } else {
	            throw new IllegalStateException("The STAFF role does not exist in the database.");
	        }

	        // Attempt to add the staff to the shelter
	        shelterService.addStaffToShelter(shelterId, staff);
	        
	        // Add a success message
	        redirectAttributes.addFlashAttribute("success", "Staff added successfully.");
	        
	        return "redirect:/shelters/" + shelterId + "/staff"; // Redirect to staff list
	    } catch (DataIntegrityViolationException e) {
	        // Handle case where a user with the same email already exists
	        redirectAttributes.addFlashAttribute("error", "A user with this email already exists.");
	        return "redirect:/shelters/" + shelterId + "/staff/add"; // Redirect back to form
	    } catch (Exception e) {
	        // Handle other potential exceptions
	        redirectAttributes.addFlashAttribute("error", "An error occurred while adding staff.");
	        return "redirect:/shelters/" + shelterId + "/staff/add"; // Redirect back to form
	    }
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
