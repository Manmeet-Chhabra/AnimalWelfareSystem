package com.manmeet.animalsys.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.manmeet.animalsys.entity.Animal;
import com.manmeet.animalsys.entity.Role;
import com.manmeet.animalsys.entity.Shelter;
import com.manmeet.animalsys.entity.User;
import com.manmeet.animalsys.repos.RoleRepository;
import com.manmeet.animalsys.service.AnimalService;
import com.manmeet.animalsys.service.NotificationService;
import com.manmeet.animalsys.service.ShelterService;
import com.manmeet.animalsys.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/shelters")
public class ShelterController {

	@Autowired
	private ShelterService shelterService;

	@Autowired
	private AnimalService animalService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private NotificationService notificationService;

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

		// After creating a new shelter, send confirmation email to the Admin
		// Fetch admin email using findByRole method
		List<User> admins = userService.findByRole("ROLE_ADMIN");
		String adminEmail = admins.isEmpty() ? "sbp.manmeet@gmail.com" : admins.get(0).getEmail(); // Fallback to a
																									// default email if
																									// none found

		String creationSubject = "New Shelter Created";
		String creationBody = String.format(
				"Dear [Admin/Staff],\n\n" + "A new shelter has been successfully created.\n\n" + "Shelter Details:\n"
						+ "Name: %s\n" + "Location: %s\n" + "Capacity: %d\n\n"
						+ "Thank you for managing our shelters.\n\n" + "Best regards,\n" + "The Animal Welfare Team",
				shelter.getName(), shelter.getLocation(), shelter.getCapacity());
		notificationService.sendEmail(adminEmail, creationSubject, creationBody);

		// Send the same email to all staff members
	/*	List<User> staffMembers = userService.findByRole("ROLE_STAFF");
		for (User staff : staffMembers) {
			notificationService.sendEmail(staff.getEmail(), creationSubject, creationBody);
		}
*/
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

	@PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('STAFF')")
	@GetMapping("/{id}")
	public String getShelterById(@PathVariable Long id, Model model) {
	    Optional<Shelter> shelterOpt = shelterService.getShelterById(id);
	    if (shelterOpt.isPresent()) {
	        Shelter shelter = shelterOpt.get();
	        List<User> staff = shelterService.getStaffByShelter(id); // Fetch staff members
	        model.addAttribute("shelter", shelter);
	        model.addAttribute("staff", staff); // Add staff members to the model
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

		// After updating a shelter, send confirmation email to the Admin
		// Fetch admin email using findByRole method
		List<User> admins = userService.findByRole("ROLE_ADMIN");
		String adminEmail = admins.isEmpty() ? "sbp.manmeet@gmail.com" : admins.get(0).getEmail(); // Fallback to a
																									// default email if
																									// none found

		String updateSubject = "Shelter Information Updated";
		String updateBody = String.format("Dear [Admin/Staff],\n\n" + "The shelter details have been updated.\n\n"
				+ "Updated Shelter Details:\n" + "Name: %s\n" + "Location: %s\n" + "Capacity: %d\n\n"
				+ "Thank you for updating the shelter information.\n\n" + "Best regards,\n" + "The Animal Welfare Team",
				shelter.getName(), shelter.getLocation(), shelter.getCapacity());
		notificationService.sendEmail(adminEmail, updateSubject, updateBody);

		// Send the same email to all staff members
	/*	List<User> staffMembers = userService.findByRole("ROLE_STAFF");
		for (User staff : staffMembers) {
			notificationService.sendEmail(staff.getEmail(), updateSubject, updateBody);
		}
*/
		return "redirect:/shelters";
	}

	// Only Admin can delete a shelter
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/delete/{id}")
	public String deleteShelter(@PathVariable Long id) {
		shelterService.deleteShelter(id);

		// After deleting a shelter, send a notification email to the Admin
		List<User> admins = userService.findByRole("ROLE_ADMIN");
		String adminEmail = admins.isEmpty() ? "sbp.manmeet@gmail.com" : admins.get(0).getEmail(); // Fallback to a
																									// default email if
																									// none found

		String deleteSubject = "Shelter Deleted";
		String deleteBody = String
				.format("Dear [Admin/Staff],\n\n" + "The shelter has been deleted from the system.\n\n"
						+ "Shelter ID: %d\n" + "Please review the shelter list for further updates.\n\n"
						+ "Best regards,\n" + "The Animal Welfare Team", id);
		notificationService.sendEmail(adminEmail, deleteSubject, deleteBody);

		// Send the same email to all staff members
	/*	List<User> staffMembers = userService.findByRole("ROLE_STAFF");
		for (User staff : staffMembers) {
			notificationService.sendEmail(staff.getEmail(), deleteSubject, deleteBody);
		}
*/
		return "redirect:/shelters";
	}

	@PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('STAFF')")
	@GetMapping("/search")
	public String searchShelters(@RequestParam(required = false) String name,
	                             @RequestParam(required = false) String location,
	                             @RequestParam(required = false) Integer capacity,
	                             Model model) {
	    List<Shelter> shelters = shelterService.searchShelters(name, location, capacity);
	    model.addAttribute("shelters", shelters);
	    return "shelter-list"; // Ensure this matches your template name
	}
	

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

	
	    

	// Only Admin and Staff can add staff to a shelter
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	@PostMapping("/{shelterId}/staff/save")
	public String addStaffToShelter(@PathVariable Long shelterId, @RequestParam Long staffId, RedirectAttributes redirectAttributes) {
	    try {
	        // Fetch the staff member by ID
	        Optional<User> staffOpt = userService.findById1(staffId);
	        if (staffOpt.isPresent()) {
	            User staff = staffOpt.get();

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

	            // Fetch the shelter details for the email notification
	            Optional<Shelter> shelterOpt = shelterService.getShelterById(shelterId);
	            String shelterName = shelterOpt.map(Shelter::getName).orElse("Unknown Shelter");

	            // Fetch admin email using findByRole method
	            List<User> admins = userService.findByRole("ROLE_ADMIN");
	            String adminEmail = admins.isEmpty() ? "sbp.manmeet@gmail.com" : admins.get(0).getEmail(); // Fallback to a default email if none found

	            String staffEmail = staff.getEmail(); // Staff email
	            String addStaffSubject = "New Staff Added to Shelter";
	            String addStaffBody = String.format("Dear Admin,\n\n"
	                    + "A new staff member has been added to the shelter.\n\n"
	                    + "Staff Details:\n"
	                    + "Name: %s\n"
	                    + "Role: %s\n"
	                    + "Shelter: %s\n\n"
	                    + "Best regards,\n"
	                    + "The Animal Welfare Team", staff.getName(), staff.getRoles(), shelterName);

	            notificationService.sendEmail(adminEmail, addStaffSubject, addStaffBody);

	            // Notify the new staff member
	            String staffSubject = "You Have Been Added as Staff";
	            String staffBody = String.format(
	                    "Dear %s,\n\n"
	                            + "You have been successfully added as a staff member at the following shelter:\n\n"
	                            + "Shelter: %s\n"
	                            + "Role: %s\n\n"
	                            + "We welcome you to our team and look forward to your contributions.\n\n"
	                            + "Best regards,\n"
	                            + "The Animal Welfare Team",
	                    staff.getName(), shelterName, staffRole.getName());

	            notificationService.sendEmail(staffEmail, staffSubject, staffBody);

	            return "redirect:/shelters/" + shelterId + "/staff"; // Redirect to staff list
	        } else {
	            redirectAttributes.addFlashAttribute("error", "Staff member not found.");
	            return "redirect:/shelters/" + shelterId + "/staff/add"; // Redirect back to form with error
	        }
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
	public String removeStaffFromShelter(@PathVariable Long shelterId, @PathVariable Long staffId, RedirectAttributes redirectAttributes) {
	    try {
	        shelterService.removeStaffFromShelter(shelterId, staffId);

	        // Add a success message
	        redirectAttributes.addFlashAttribute("success", "Staff removed successfully.");
	    } catch (Exception e) {
	        // Handle exceptions
	        redirectAttributes.addFlashAttribute("error", "An error occurred while removing staff.");
	    }

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

	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	@GetMapping("/{shelterId}/add")
	public String addAnimalForm(@PathVariable Long shelterId, Model model) {
		model.addAttribute("animal", new Animal());
		model.addAttribute("shelterId", shelterId);
		return "shelter-animal-add";
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	@PostMapping("/{shelterId}/add")
	public String addAnimal(@PathVariable Long shelterId, @ModelAttribute @Valid Animal animal, BindingResult result,
			@RequestParam("fileUpload") MultipartFile file) {
		if (result.hasErrors()) {
			return "animal-add"; // The name of your form view
		}
		if (!file.isEmpty()) {
			try {
				// Save the file data as BLOB
				animal.setPictureData(file.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		animalService.addAnimal(shelterId, animal);
		// Fetch admin email using findByRole method
		List<User> admins = userService.findByRole("ROLE_ADMIN");
		String adminEmail = admins.isEmpty() ? "sbp.manmeet@gmail.com" : admins.get(0).getEmail();
		// Notify admin about the new animal addition
		String subject = "New Animal Added to Shelter";
		String body = String.format(
				"Dear Admin,\n\n" + "A new animal has been successfully added to Shelter ID %d:\n\n" + "Name: %s\n"
						+ "Type: %s\n" + "Health Status: %s\n\n" + "Please review the details at your convenience.\n\n"
						+ "Best regards,\n" + "Animal Welfare Team",
				shelterId, animal.getName(), animal.getType(), animal.getHealthStatus());
		notificationService.sendEmail(adminEmail, subject, body);
		// Send the same email to all staff members
		/*
		 * List<User> staffMembers = userService.findByRole("ROLE_STAFF"); for (User
		 * staff : staffMembers) { String staffEmail = staff.getEmail();
		 * notificationService.sendEmail(staffEmail, subject, body); }
		 */
		return "redirect:/shelter/" + shelterId + "/animals";
		// Redirect to the list of animals in the shelter
	}

	// Only Admin and Staff can view the form to manage shelter capacity
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	@GetMapping("/{id}/capacity")
	public String manageCapacityForm(@PathVariable Long id, Model model) {
	    Optional<Shelter> shelterOpt = shelterService.getShelterById(id);
	    if (shelterOpt.isPresent()) {
	        model.addAttribute("shelter", shelterOpt.get());
	        return "shelter-capacity";
	    } else {
	        return "redirect:/shelters"; // Redirect to shelter list if shelter not found
	    }
	}

	// Only Admin and Staff can increase shelter capacity
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	@PostMapping("/{id}/capacity/increase")
	public String increaseCapacity(@PathVariable Long id, @RequestParam("increment") int increment, RedirectAttributes redirectAttributes) {
	    try {
	        shelterService.increaseCapacity(id, increment);
	        redirectAttributes.addFlashAttribute("success", "Capacity increased successfully.");
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("error", "An error occurred while increasing capacity.");
	    }
	    return "redirect:/shelters/" + id + "/capacity";
	}

	// Only Admin and Staff can decrease shelter capacity
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	@PostMapping("/{id}/capacity/decrease")
	public String decreaseCapacity(@PathVariable Long id, @RequestParam("decrement") int decrement, RedirectAttributes redirectAttributes) {
	    try {
	        shelterService.decreaseCapacity(id, decrement);
	        redirectAttributes.addFlashAttribute("success", "Capacity decreased successfully.");
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("error", "An error occurred while decreasing capacity.");
	    }
	    return "redirect:/shelters/" + id + "/capacity";
	}

}