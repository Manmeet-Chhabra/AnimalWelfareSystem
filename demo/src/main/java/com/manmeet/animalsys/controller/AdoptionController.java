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

import jakarta.persistence.EntityNotFoundException;

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
			Model model, Principal principal) {

		// Extract the answers from the map
		List<String> answers = new ArrayList<>();

		// Loop through the parameters to get the answers
		for (String key : allParams.keySet()) {
			if (key.startsWith("answer")) { // Assuming your answer fields are named like answer1, answer2, etc.
				answers.add(allParams.get(key));
			}
		}

		// Calculate score based on the answers
		int score = adoptionService.evaluateScore(answers);

		// Fetch the current user based on the principal
		User currentUser = userService.findByEmail(principal.getName());

		// Create a new Adoption object
		Adoption adoptionRequest = new Adoption();
		adoptionRequest.setUser(currentUser);
		adoptionRequest.setAnimal(
				animalService.getAnimalById(animalId).orElseThrow(() -> new RuntimeException("Animal not found")));
		adoptionRequest.setStatus(AdoptionRequestStatus.PENDING);
		adoptionRequest.setRequestDate(LocalDate.now());

		// Save the adoption request to the database
		Adoption savedAdoptionRequest = adoptionService.createAdoptionRequest(adoptionRequest);

		// Save the adoption answers
		adoptionService.saveAdoptionAnswers(savedAdoptionRequest, answers);

		// Set eligibility status based on score
		if (score >= 75) {
			model.addAttribute("animalId", animalId);
			model.addAttribute("eligibilityStatus", "Eligible for adoption");
			return "adoption-request-form"; // Redirect to the request form
		} else if (score >= 40 && score < 75) {
			model.addAttribute("eligibilityStatus", "Pending admin review");
			return "adoption-result"; // Redirect to the result page
		} else {
			model.addAttribute("eligibilityStatus", "Not eligible for adoption");
			model.addAttribute("feedback", "Your score is below the required threshold. Please review our guidelines.");
			return "adoption-result"; // Redirect to the result page
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

	/*
	 * @GetMapping("/{animalId}/request")
	 * 
	 * @PreAuthorize("hasRole('USER')") public String
	 * showAdoptionRequestForm(@PathVariable Long animalId, Model model) { // Get
	 * the current user's authentication Authentication authentication =
	 * SecurityContextHolder.getContext().getAuthentication();
	 * 
	 * // Get the user details (Assuming your User class implements UserDetails)
	 * User currentUser = (User) authentication.getPrincipal(); // Replace with your
	 * User class
	 * 
	 * Long userId = currentUser.getId(); // Assuming getId() returns the user ID
	 * 
	 * model.addAttribute("animalId", animalId); model.addAttribute("userId",
	 * userId); // Add userId to the model return "adoption-request-form"; // Return
	 * the name of your Thymeleaf template }
	 */

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

	/*
	 * @PostMapping("/{animalId}/submit")
	 * 
	 * @PreAuthorize("hasRole('USER')") public String
	 * submitAdoptionRequest(@PathVariable Long animalId, @RequestParam Long userId,
	 * RedirectAttributes redirectAttributes) { // Log the received parameters for
	 * debugging System.out.println("Animal ID: " + animalId);
	 * System.out.println("User ID: " + userId);
	 * 
	 * // Check if userId is not null before proceeding if (userId == null) {
	 * redirectAttributes.addFlashAttribute("errorMessage",
	 * "User ID must be provided."); return "redirect:/adoption-error"; // Redirect
	 * to an error page or handle as needed }
	 * 
	 * Animal animal = animalService.getAnimalById(animalId) .orElseThrow(() -> new
	 * RuntimeException("Animal not found"));
	 * 
	 * User user = userService.getUserById(userId).orElseThrow(() -> new
	 * RuntimeException("User not found"));
	 * 
	 * // Create and populate the Adoption object Adoption adoptionRequest = new
	 * Adoption(); adoptionRequest.setAnimal(animal); adoptionRequest.setUser(user);
	 * adoptionRequest.setStatus(AdoptionRequestStatus.PENDING); // Set initial
	 * status adoptionRequest.setRequestDate(LocalDate.now());
	 * 
	 * // Call the service method to save it
	 * adoptionService.createAdoptionRequest(adoptionRequest);
	 * 
	 * // Add flash attributes for feedback
	 * redirectAttributes.addFlashAttribute("message",
	 * "Your adoption request has been submitted and is pending review.");
	 * redirectAttributes.addFlashAttribute("adoptionRequest", adoptionRequest);
	 * 
	 * // Redirect to the status page return "redirect:/adoption-status"; // Ensure
	 * this path is correct }
	 * 
	 * @PostMapping("/{animalId}/submit")
	 * 
	 * @PreAuthorize("hasRole('USER')") public String
	 * submitAdoptionRequest(@PathVariable Long animalId, RedirectAttributes
	 * redirectAttributes, Principal principal) { // Fetch the current user User
	 * currentUser = userService.findByEmail(principal.getName()); Long userId =
	 * currentUser.getId();
	 * 
	 * // Log the received parameters for debugging System.out.println("Animal ID: "
	 * + animalId); System.out.println("User ID: " + userId);
	 * 
	 * // Proceed with the adoption request logic Animal animal =
	 * animalService.getAnimalById(animalId) .orElseThrow(() -> new
	 * RuntimeException("Animal not found"));
	 * 
	 * User user = userService.getUserById(userId).orElseThrow(() -> new
	 * RuntimeException("User not found"));
	 * 
	 * Adoption adoptionRequest = new Adoption(); adoptionRequest.setAnimal(animal);
	 * adoptionRequest.setUser(user);
	 * adoptionRequest.setStatus(AdoptionRequestStatus.PENDING);
	 * adoptionRequest.setRequestDate(LocalDate.now());
	 * 
	 * // Save the adoption request
	 * adoptionService.createAdoptionRequest(adoptionRequest);
	 * 
	 * // Add flash attributes for feedback
	 * redirectAttributes.addFlashAttribute("message",
	 * "Your adoption request has been submitted and is pending review.");
	 * redirectAttributes.addFlashAttribute("adoptionRequest", adoptionRequest);
	 * 
	 * // Redirect to the status page return "redirect:/adoptions/adoption-status";
	 * }
	 */

	@PostMapping("/{animalId}/submit")
	@PreAuthorize("hasRole('USER')")
	public String submitAdoptionRequest(@PathVariable Long animalId, RedirectAttributes redirectAttributes,
			Principal principal) {
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
		redirectAttributes.addFlashAttribute("message",
				"Your adoption request has been submitted and is pending review.");
		redirectAttributes.addFlashAttribute("adoptionRequest", adoptionRequest);

		// Redirect to the status page
		return "redirect:/adoptions/adoption-status";
	}

	// Admin review adoption requests
	@GetMapping("/requests")
	@PreAuthorize("hasRole('ADMIN')")
	public String listAdoptionRequests(Model model) {
		List<Adoption> pendingRequests = adoptionService.getAllAdoptionRequests(); // Ensure this method is
																							// defined correctly
		model.addAttribute("pendingRequests", pendingRequests);
		System.out.println("Fetched pending requests: " + pendingRequests);
		model.addAttribute("pendingRequests", pendingRequests);
		return "admin-review"; // or whatever your view is called
	}
	
	

	@GetMapping("/review/{adoptionId}")
	@PreAuthorize("hasRole('ADMIN')")
	public String reviewAdoptionRequestDetails(@PathVariable Long adoptionId, Model model) {
		var adoptionRequest = adoptionService.getAdoptionRequestById(adoptionId);

		if (adoptionRequest == null) {
			model.addAttribute("errorMessage", "No adoption request found.");
			return "error-page"; // Update to your error page
		}

		model.addAttribute("adoptionRequest", adoptionRequest);
		return "adoption-request-review"; // Update to your specific review page
	}
	
	
	
	
	@PostMapping("/update-status")
	@PreAuthorize("hasRole('ADMIN')")
	public String updateAdoptionRequestStatus(@RequestParam Long requestId, 
	                                           @RequestParam String decision) {
	    // Logic to update the request status in the database
	    Adoption adoptionRequest = adoptionService.getAdoptionRequestById(requestId);
	    
	    if (adoptionRequest != null) {
	        // Set the status based on the decision provided
	        try {
	            // Convert the string decision to the AdoptionRequestStatus enum
	            AdoptionRequestStatus status = AdoptionRequestStatus.valueOf(decision.toUpperCase());
	            adoptionRequest.setStatus(status);
	        } catch (IllegalArgumentException e) {
	            // Handle the case where the decision is not valid
	            throw new IllegalArgumentException("Invalid decision provided: " + decision);
	        }

	        // Save the updated request
	        adoptionService.save(adoptionRequest);
	    } else {
	        // Handle the case where the request is not found
	        throw new EntityNotFoundException("Adoption request not found."); // Ensure this exception is handled appropriately
	    }
	    
	    // Redirect back to the pending requests page after updating
	    return "redirect:/adoptions/requests"; // Ensure this path is correct
	}




	/*
	 * @GetMapping("/answers/{id}")
	 * 
	 * @PreAuthorize("hasRole('ADMIN')") public String
	 * viewAdoptionAnswers(@PathVariable Long id, Model model) { Adoption adoption =
	 * adoptionService.findById(id) .orElseThrow(() -> new
	 * RuntimeException("Adoption request not found"));
	 * 
	 * List<String> answers = adoptionService.getAdoptionAnswers(id); // You need to
	 * implement this method to retrieve answers.
	 * 
	 * model.addAttribute("adoption", adoption); model.addAttribute("answers",
	 * answers);
	 * 
	 * return "admin/adoption-answers"; // Create this view to display answers }
	 */

	@GetMapping("/answers/{adoptionId}")
	@PreAuthorize("hasRole('ADMIN')")
	public String viewAdoptionAnswers(@PathVariable Long adoptionId, Model model) {
	    // Fetch the adoption request using the adoption ID
	    var adoptionRequest = adoptionService.getAdoptionRequestById(adoptionId);
	    
	    // Check if the adoption request exists
	    if (adoptionRequest == null) {
	        model.addAttribute("errorMessage", "Adoption request not found.");
	        return "error-page"; // Return to your error page
	    }

	    // Fetch the answers associated with the adoption request
	    var answers = adoptionService.getAdoptionAnswers(adoptionId);

	    // Check if answers exist
	    if (answers == null || answers.isEmpty()) {
	        model.addAttribute("errorMessage", "No answers found for this adoption request.");
	        model.addAttribute("adoption", adoptionRequest); // Still include the adoption request
	        return "error-page"; // Return to your error page
	    }

	    // Add necessary attributes to the model
	    model.addAttribute("adoption", adoptionRequest); // Include the adoption request in the model
	    model.addAttribute("answers", answers); // Include answers from the database

	    // Optional: Predefined questions, if you want to map answers to questions in the view
	    List<String> questions = List.of(
	        "What is your experience with animals?",
	        "What is your living situation?",
	        "How much time can you dedicate to a pet?",
	        "Are you financially prepared for a pet?",
	        "What is your lifestyle like?",
	        "What knowledge or training do you have?",
	        "Do you own or rent your home?",
	        "Do you have plans for who will care for the animal if you are unable?",
	        "Are you willing to undergo a home visit?",
	        "Do you have any allergies to animals?"
	    );

	    model.addAttribute("questions", questions); // Include predefined questions if needed

	    return "adoption-answers"; // Return to your view page
	}





	
	


	@PostMapping("/requests/{requestId}/review")
	@PreAuthorize("hasRole('ADMIN')")
	public String reviewAdoptionRequest(@PathVariable Long requestId, @RequestParam("status") String status,
			Model model) {
		try {
			AdoptionRequestStatus requestStatus = AdoptionRequestStatus.valueOf(status.toUpperCase());
			adoptionService.updateRequestStatus(requestId, requestStatus);
			model.addAttribute("message", "Request has been updated to " + status);
		} catch (IllegalArgumentException e) {
			model.addAttribute("errorMessage", "Invalid status provided.");
			return "error-page"; // Update to your error page
		} catch (Exception e) {
			model.addAttribute("errorMessage", "Failed to update the request status.");
			return "error-page"; // Update to your error page
		}

		return "redirect:/adoptions/requests"; // Redirect to avoid form resubmission
	}

}