package com.manmeet.animalsys.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.ResponseStatus;

import com.manmeet.animalsys.entity.Animal;
import com.manmeet.animalsys.entity.Shelter;
import com.manmeet.animalsys.entity.User;
import com.manmeet.animalsys.service.AnimalService;
import com.manmeet.animalsys.service.NotificationService;
import com.manmeet.animalsys.service.ShelterService;
import com.manmeet.animalsys.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/animals")
public class AnimalController {

	private static final Logger logger = LoggerFactory.getLogger(AnimalController.class);

	// private final AnimalService animalService;

	@Autowired
	private ShelterService shelterService;

	@Autowired
	private AnimalService animalService;

	@Autowired
	private UserService userService;

	@Autowired
	private NotificationService notificationService;

	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	@GetMapping("/create")
	public String createAnimalForm(Model model) {
		model.addAttribute("animal", new Animal());
		model.addAttribute("shelters", shelterService.getAllShelters()); // Fetch and add shelters to the model
		return "animal-create";
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	@PostMapping("/create")
	public String createAnimal(@ModelAttribute Animal animal) {
		animalService.saveAnimal(animal);

		// Fetch admin email using findByRole method
		List<User> admins = userService.findByRole("ROLE_ADMIN");
		String adminEmail;

		// Check if any admins were found
		if (!admins.isEmpty()) {
			adminEmail = admins.get(0).getEmail(); // Get the first admin's email
		} else {
			adminEmail = "sbp.manmeet@gmail.com"; // Fallback to hardcoded admin email
		}

		// Send notification to the admin about the new animal
		String subject = "New Animal Created";
		String body = String.format("Dear Admin,\n\n"
				+ "We are pleased to inform you that a new animal has been successfully added to the system:\n\n"
				+ "Name: %s\n" + "Type: %s\n" + "Health Status: %s\n\n"
				+ "Please review the details at your earliest convenience.\n\n" + "Best regards,\n"
				+ "Animal Welfare Team", animal.getName(), animal.getType(), animal.getHealthStatus());

		notificationService.sendEmail(adminEmail, subject, body);
		
		// Send the same email to all staff members
		List<User> staffMembers = userService.findByRole("ROLE_STAFF");
		for (User staff : staffMembers) {
		    String staffEmail = staff.getEmail();
		    notificationService.sendEmail(staffEmail, subject, body);
		}
		return "redirect:/animals";
	}

	@PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('STAFF')")
	@GetMapping
	public String getAllAnimals(Model model) {
		List<Animal> animals = animalService.getAllAnimals();
		model.addAttribute("animals", animals);
		return "animal-list";
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	@GetMapping("/{id}")
	public String getAnimalById(@PathVariable Long id, Model model) {
		Optional<Animal> animal = animalService.getAnimalById(id);
		if (animal.isPresent()) {
			model.addAttribute("animal", animal.get());
			return "animal-details";
		}
		logger.warn("Animal with ID {} not found", id);
		return "error-page"; // Use a more descriptive error page if necessary
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	@GetMapping("/{id}/edit")
	public String editAnimalForm(@PathVariable Long id, Model model) {
		Optional<Animal> animal = animalService.getAnimalById(id);
		if (animal.isPresent()) {
			model.addAttribute("animal", animal.get());

			// Fetch all shelters to populate the dropdown
			List<Shelter> shelters = shelterService.getAllShelters();
			model.addAttribute("shelters", shelters);

			return "animal-edit"; // Thymeleaf template for editing the animal
		}
		logger.warn("Attempt to edit non-existent animal with ID {}", id);
		return "error-page"; // Return an error page if the animal does not exist
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	@PostMapping("/{id}/edit")
	public String updateAnimal(@PathVariable Long id, @ModelAttribute Animal animal) {
		animal.setId(id); // Ensure the ID is set in the animal object
		animalService.updateAnimal(id, animal); // Update the animal in the database

		// Fetch admin email using findByRole method
		List<User> admins = userService.findByRole("ROLE_ADMIN");
		String adminEmail;

		// Check if any admins were found
		if (!admins.isEmpty()) {
			adminEmail = admins.get(0).getEmail(); // Get the first admin's email
		} else {
			adminEmail = "sbp.manmeet@gmail.com"; // Fallback to hardcoded admin email
		}

		// Send notification to the admin about the animal update
		String subject = "Animal Updated";
		String body = String.format("Dear Admin,\n\n"
				+ "The details for an existing animal have been updated successfully:\n\n" + "Name: %s\n" + "Type: %s\n"
				+ "Health Status: %s\n\n" + "Please take a moment to review the updated information.\n\n"
				+ "Best regards,\n" + "Animal Welfare Team", animal.getName(), animal.getType(),
				animal.getHealthStatus());

		notificationService.sendEmail(adminEmail, subject, body);
		
		// Send the same email to all staff members
		List<User> staffMembers = userService.findByRole("ROLE_STAFF");
		for (User staff : staffMembers) {
		    String staffEmail = staff.getEmail();
		    notificationService.sendEmail(staffEmail, subject, body);
		}

		return "redirect:/animals"; // Redirect to the animal list after updating
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/{id}/delete")
	public String deleteAnimal(@PathVariable Long id) {
		animalService.deleteAnimal(id);

		// Fetch admin email using findByRole method
		List<User> admins = userService.findByRole("ROLE_ADMIN");
		String adminEmail;

		// Check if any admins were found
		if (!admins.isEmpty()) {
			adminEmail = admins.get(0).getEmail(); // Get the first admin's email
		} else {
			adminEmail = "sbp.manmeet@gmail.com"; // Fallback to hardcoded admin email
		}

		// Notify admin about the animal deletion
		String subject = "Animal Deleted";
		String body = String.format("Dear Admin,\n\n"
				+ "We want to inform you that the following animal has been deleted from the system:\n\n" + "ID: %d\n\n"
				+ "If you have any questions or need further assistance, please do not hesitate to reach out.\n\n"
				+ "Best regards,\n" + "Animal Welfare Team", id);
		notificationService.sendEmail(adminEmail, subject, body);

		return "redirect:/animals";
	}

	@PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('STAFF')")
	@GetMapping("/search")
	public String searchAnimals(@RequestParam(required = false) String type,
			@RequestParam(required = false) String healthStatus, Model model) {
		List<Animal> animals = animalService.searchAnimals(type, healthStatus);
		model.addAttribute("animals", animals);
		return "animal-search"; // Ensure this matches your template name
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	@GetMapping("/shelter/{shelterId}/add")
	public String addAnimalForm(@PathVariable Long shelterId, Model model) {
		model.addAttribute("animal", new Animal());
		model.addAttribute("shelterId", shelterId);
		return "animal-add";
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	@PostMapping("/shelter/{shelterId}/add")
	public String addAnimal(@PathVariable Long shelterId, @ModelAttribute @Valid Animal animal, BindingResult result) {
		if (result.hasErrors()) {
			return "animal-add"; // The name of your form view
		}
		animalService.addAnimal(shelterId, animal);
		
		// Fetch admin email using findByRole method
				List<User> admins = userService.findByRole("ROLE_ADMIN");
				String adminEmail;

				// Check if any admins were found
				if (!admins.isEmpty()) {
					adminEmail = admins.get(0).getEmail(); // Get the first admin's email
				} else {
					adminEmail = "sbp.manmeet@gmail.com"; // Fallback to hardcoded admin email
				}
		
		// Notify admin about the new animal addition
				String subject = "New Animal Added to Shelter";
				String body = String.format(
				    "Dear Admin,\n\n" +
				    "A new animal has been successfully added to Shelter ID %d:\n\n" +
				    "Name: %s\n" +
				    "Type: %s\n" +
				    "Health Status: %s\n\n" +
				    "Please review the details at your convenience.\n\n" +
				    "Best regards,\n" +
				    "Animal Welfare Team",
				    shelterId, animal.getName(), animal.getType(), animal.getHealthStatus());

        notificationService.sendEmail(adminEmail, subject, body);
        
     // Send the same email to all staff members
        List<User> staffMembers = userService.findByRole("ROLE_STAFF");
        for (User staff : staffMembers) {
            String staffEmail = staff.getEmail();
            notificationService.sendEmail(staffEmail, subject, body);
        }
		
		return "redirect:/animals/shelter/" + shelterId;
	}

	@PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('STAFF')")
	@GetMapping("/shelter/{shelterId}")
	public String getAnimalsByShelter(@PathVariable Long shelterId, Model model) {
		List<Animal> animals = animalService.getAnimalsByShelter(shelterId);
		model.addAttribute("animals", animals);
		model.addAttribute("shelterId", shelterId);
		return "animal-shelter-list";
	}

	@GetMapping("/animals/check/{name}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Void> checkAnimalExists(@PathVariable String name) {
		Animal animal = animalService.findByName(name);
		return animal != null ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
	}

}
