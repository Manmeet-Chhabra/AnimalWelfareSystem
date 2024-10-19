package com.manmeet.animalsys.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.manmeet.animalsys.entity.Adoption;
import com.manmeet.animalsys.entity.AdoptionRequestStatus;
import com.manmeet.animalsys.entity.AdoptionStatus;
import com.manmeet.animalsys.entity.Animal;
import com.manmeet.animalsys.entity.User;
import com.manmeet.animalsys.service.AdoptionService;
import com.manmeet.animalsys.service.AnimalService;
import com.manmeet.animalsys.service.UserService;

@Controller
@RequestMapping("/adoptions")
public class AdoptionController {

	@Autowired
	private AdoptionService adoptionService;

	@Autowired
	private AnimalService animalService;

	@Autowired
	private UserService userService; // Assuming you have a UserService

	
	
	// Display list of animals available for adoption
	@GetMapping("/available")
	public String listAvailableAnimals(Model model) {
		List<Animal> availableAnimals = animalService.getAvailableAnimals();

		model.addAttribute("animals", availableAnimals);

		return "adopt-animal-list";
	}

	// Show adoption eligibility questionnaire form
	@GetMapping("/{animalId}/questionnaire")
	@PreAuthorize("hasRole('USER')")
	public String showQuestionnaire(@PathVariable Long animalId, Model model) {
		model.addAttribute("animalId", animalId);
		return "adoption-questionnaire";
	}

	@PostMapping("/{animalId}/questionnaire")
	@PreAuthorize("hasRole('USER')")
	public String submitQuestionnaire(@PathVariable Long animalId, @RequestParam Map<String, String> allParams,
			Model model) {
		// Extract the answers from the map
		List<String> answers = new ArrayList<>();

		// Loop through the parameters to get the answers
		for (String key : allParams.keySet()) {
			if (key.startsWith("answer")) { // Assuming your answer fields are named like answer1, answer2, etc.
				answers.add(allParams.get(key));
			}
		}

		int score = adoptionService.evaluateScore(answers);

		if (score >= 75) {
			model.addAttribute("animalId", animalId);
			model.addAttribute("eligibilityStatus", "Eligible for adoption");
			return "adoption-request-form";
		} else if (score >= 40 && score < 75) {
			model.addAttribute("eligibilityStatus", "Pending admin review");
			return "adoption-result";
		} else {
			model.addAttribute("eligibilityStatus", "Not eligible for adoption");
			model.addAttribute("feedback", "Your score is below the required threshold. Please review our guidelines.");
			return "adoption-result";
		}
	}

	@GetMapping("/adoption-status")
	@PreAuthorize("hasRole('USER')")
	public String checkAdoptionStatus(Principal principal, Model model) {
	    // Fetch the current user by email
	    User currentUser = userService.findByEmail(principal.getName());
	    
	    // Retrieve the user's adoption requests
	    List<Adoption> userAdoptions = adoptionService.getAdoptionsByUserId(currentUser.getId());
	    
	    // Add the list of adoptions to the model
	    model.addAttribute("adoptions", userAdoptions);
	    
	    return "adoption-status"; // Return the Thymeleaf template
	}

	
	

	/* @GetMapping("/{animalId}/request")
	    @PreAuthorize("hasRole('USER')")
	    public String showAdoptionRequestForm(@PathVariable Long animalId, Model model) {
	        // Get the current user's authentication
	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        
	        // Get the user details (Assuming your User class implements UserDetails)
	        User currentUser = (User) authentication.getPrincipal(); // Replace with your User class
	        
	        Long userId = currentUser.getId(); // Assuming getId() returns the user ID

	        model.addAttribute("animalId", animalId);
	        model.addAttribute("userId", userId); // Add userId to the model
	        return "adoption-request-form"; // Return the name of your Thymeleaf template
	    } */
	
	 @GetMapping("/{animalId}/request")
	 @PreAuthorize("hasRole('USER')")
	 public String showAdoptionRequestForm(@PathVariable Long animalId, Model model, Principal principal) {
	     // Fetch current user from Principal
	     User currentUser = userService.findByEmail(principal.getName()); // Assuming email is unique
	     
	     Long userId = currentUser.getId();

	     model.addAttribute("animalId", animalId);
	     model.addAttribute("userId", userId); // Add userId to the model
	     return "adoption-request-form";
	 }


	/*@PostMapping("/{animalId}/submit")
	@PreAuthorize("hasRole('USER')")
	public String submitAdoptionRequest(@PathVariable Long animalId, @RequestParam Long userId,
			RedirectAttributes redirectAttributes) {
		// Log the received parameters for debugging
		System.out.println("Animal ID: " + animalId);
		System.out.println("User ID: " + userId);

		// Check if userId is not null before proceeding
		if (userId == null) {
			redirectAttributes.addFlashAttribute("errorMessage", "User ID must be provided.");
			return "redirect:/adoption-error"; // Redirect to an error page or handle as needed
		}

		Animal animal = animalService.getAnimalById(animalId)
				.orElseThrow(() -> new RuntimeException("Animal not found"));

		User user = userService.getUserById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		// Create and populate the Adoption object
		Adoption adoptionRequest = new Adoption();
		adoptionRequest.setAnimal(animal);
		adoptionRequest.setUser(user);
		adoptionRequest.setStatus(AdoptionRequestStatus.PENDING); // Set initial status
		adoptionRequest.setRequestDate(LocalDate.now());

		// Call the service method to save it
		adoptionService.createAdoptionRequest(adoptionRequest);

		// Add flash attributes for feedback
		redirectAttributes.addFlashAttribute("message",
				"Your adoption request has been submitted and is pending review.");
		redirectAttributes.addFlashAttribute("adoptionRequest", adoptionRequest);

		// Redirect to the status page
		return "redirect:/adoption-status"; // Ensure this path is correct
	} 
	
	@PostMapping("/{animalId}/submit")
	@PreAuthorize("hasRole('USER')")
	public String submitAdoptionRequest(@PathVariable Long animalId, RedirectAttributes redirectAttributes, Principal principal) {
	    // Fetch the current user
	    User currentUser = userService.findByEmail(principal.getName());
	    Long userId = currentUser.getId();

	    // Log the received parameters for debugging
	    System.out.println("Animal ID: " + animalId);
	    System.out.println("User ID: " + userId);

	    // Proceed with the adoption request logic
	    Animal animal = animalService.getAnimalById(animalId)
	            .orElseThrow(() -> new RuntimeException("Animal not found"));

	    User user = userService.getUserById(userId).orElseThrow(() -> new RuntimeException("User not found"));

	    Adoption adoptionRequest = new Adoption();
	    adoptionRequest.setAnimal(animal);
	    adoptionRequest.setUser(user);
	    adoptionRequest.setStatus(AdoptionRequestStatus.PENDING);
	    adoptionRequest.setRequestDate(LocalDate.now());

	    // Save the adoption request
	    adoptionService.createAdoptionRequest(adoptionRequest);

	    // Add flash attributes for feedback
	    redirectAttributes.addFlashAttribute("message", "Your adoption request has been submitted and is pending review.");
	    redirectAttributes.addFlashAttribute("adoptionRequest", adoptionRequest);

	    // Redirect to the status page
	    return "redirect:/adoptions/adoption-status";
	} */

	 @PostMapping("/{animalId}/submit")
	 @PreAuthorize("hasRole('USER')")
	 public String submitAdoptionRequest(@PathVariable Long animalId, RedirectAttributes redirectAttributes, Principal principal) {
	     // Fetch the current user
	     User currentUser = userService.findByEmail(principal.getName());
	     Long userId = currentUser.getId();

	     // Log the received parameters for debugging
	     System.out.println("Animal ID: " + animalId);
	     System.out.println("User ID: " + userId);

	     // Fetch the animal by ID
	     Animal animal = animalService.getAnimalById(animalId)
	             .orElseThrow(() -> new RuntimeException("Animal not found"));

	     // Check if the animal is available for adoption
	     if (animal.getAdoptionStatus() != AdoptionStatus.AVAILABLE) {
	         redirectAttributes.addFlashAttribute("error", "This animal is no longer available for adoption.");
	         return "redirect:/adoptions/adoption-status"; // Redirect if not available
	     }

	     // Create a new Animal object for the update
	     Animal updatedAnimal = new Animal();
	     updatedAnimal.setId(animalId); // Set the ID to ensure it updates the correct animal
	     updatedAnimal.setName(animal.getName());
	     updatedAnimal.setType(animal.getType());
	     updatedAnimal.setHealthStatus(animal.getHealthStatus());
	     updatedAnimal.setDoctorAppointment(animal.getDoctorAppointment());
	     updatedAnimal.setPictureUrl(animal.getPictureUrl());
	     updatedAnimal.setShelter(animal.getShelter());
	     updatedAnimal.setAdoptionStatus(AdoptionStatus.ADOPTED); // Set the adoption status to ADOPTED

	     // Update animal status to not available for adoption using the service
	     animalService.updateAnimal(animalId, updatedAnimal); // Call the update method with ID and updated animal object

	     // Fetch the user by ID
	     User user = userService.getUserById(userId).orElseThrow(() -> new RuntimeException("User not found"));

	     // Create a new adoption request
	     Adoption adoptionRequest = new Adoption();
	     adoptionRequest.setAnimal(updatedAnimal); // Use the updated animal
	     adoptionRequest.setUser(user);
	     adoptionRequest.setStatus(AdoptionRequestStatus.PENDING);
	     adoptionRequest.setRequestDate(LocalDate.now());

	     // Save the adoption request
	     adoptionService.createAdoptionRequest(adoptionRequest);

	     // Add flash attributes for feedback
	     redirectAttributes.addFlashAttribute("message", "Your adoption request has been submitted and is pending review.");
	     redirectAttributes.addFlashAttribute("adoptionRequest", adoptionRequest);

	     // Redirect to the status page
	     return "redirect:/adoptions/adoption-status";
	 }

	
	// Admin review adoption requests
	@GetMapping("/requests")
	@PreAuthorize("hasRole('ADMIN')")
	public String listAdoptionRequests(Model model) {
		List<Adoption> requests = adoptionService.getAllAdoptionRequests();
		model.addAttribute("requests", requests);
		return "admin-review";
	}

	@PostMapping("/requests/{requestId}/review")
	@PreAuthorize("hasRole('ADMIN')")
	public String reviewAdoptionRequest(@PathVariable Long requestId, @RequestParam("status") String status,
			Model model) {
		AdoptionRequestStatus requestStatus = AdoptionRequestStatus.valueOf(status.toUpperCase());
		adoptionService.updateRequestStatus(requestId, requestStatus);

		model.addAttribute("message", "Request has been updated to " + status);
		return "redirect:/adoptions/requests"; // Redirect to avoid form resubmission
	}

}