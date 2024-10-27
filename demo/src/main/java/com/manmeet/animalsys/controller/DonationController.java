package com.manmeet.animalsys.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.manmeet.animalsys.entity.Donation;
import com.manmeet.animalsys.entity.DonationGoal;
import com.manmeet.animalsys.entity.DonationType;
import com.manmeet.animalsys.service.DonationGoalService;
import com.manmeet.animalsys.service.DonationService;

@Controller
@RequestMapping("/donations")
public class DonationController {

	@Autowired
	private DonationService donationService;

	@Autowired
	private DonationGoalService donationGoalService;

	// Display the main donation page with dropdown
	@GetMapping
	public String showMainPage(Model model) {
		return "main-donation-page"; // Thymeleaf template name for main page(index)
	}

	@GetMapping("/{type}")
	public String showDonationPage(@PathVariable("type") String type, Model model) {

		System.out.println("Accessing donation type: " + type); // Debug log
		Logger logger = LoggerFactory.getLogger(DonationController.class);
		logger.info("Requested donation type: {}", type); // Log requested donation type
		// Convert type to uppercase
		String donationType = type.toUpperCase();

		// Validate the donation type using an enum
		try {
			DonationType.valueOf(donationType); // This will throw an IllegalArgumentException if the type is
												// unsupported
		} catch (IllegalArgumentException e) {
			logger.warn("Unsupported donation type: {}", donationType);
			return "error-page"; // Return a specific error page template
		}

		model.addAttribute("donationType", donationType);
		return "donation-" + type.toLowerCase();

	}

	// Display the donation form - allowed for all users
	@GetMapping("/new")
	@PreAuthorize("isAuthenticated()") // Ensure user is authenticated
	public String showDonationForm(Model model) {
		model.addAttribute("donation", new Donation());
		return "donation-form"; // Thymeleaf template name
	}

	// Save a new donation - allowed for all users
	@PostMapping("/save")
	@PreAuthorize("isAuthenticated()") // Ensure user is authenticated
	public String saveDonation(@ModelAttribute Donation donation) {
		donation.setDate(LocalDate.now()); // Set current date
		donationService.saveDonation(donation);
		return "thank-you"; // Redirect to thank you page
	}

	// Display all donations - restricted to admin or authorized users
	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')") // Only admins can view all donations
	public String listDonations(Model model) {
		List<Donation> donations = donationService.getAllDonations();
		model.addAttribute("donations", donations);
		return "donation-list"; // Thymeleaf template name
	}

	// Delete a donation - restricted to admin
	@GetMapping("/delete/{id}")
	@PreAuthorize("hasRole('ADMIN')") // Only admins can delete donations
	public String deleteDonation(@PathVariable Long id) {
		donationService.deleteDonation(id);
		return "redirect:/donations/admin"; // Redirect to the donation list for admin
	}

	// -------------Donationgoal-----------
	@GetMapping("/donation-progress")
	public String showDonationProgress(Model model) {
	    try {
	        DonationGoal goal = donationGoalService.getCurrentGoal();

	        // Log the goal values
	        System.out.println("Goal Amount: " + goal.getGoalAmount());
	        System.out.println("Current Total: " + goal.getCurrentTotal());

	        model.addAttribute("goalAmount", goal.getGoalAmount() != null ? goal.getGoalAmount().doubleValue() : 0.0);
	        model.addAttribute("currentTotal", goal.getCurrentTotal() != null ? goal.getCurrentTotal().doubleValue() : 0.0);
	    } catch (NoSuchElementException e) {
	        model.addAttribute("goalAmount", 0.0);
	        model.addAttribute("currentTotal", 0.0);
	        model.addAttribute("message", "No current donation goal found.");
	    }
	    return "donation-progress"; 
	}





	@GetMapping("/set-goal")
	public String showSetGoalPage(Model model) {
		// Optionally add any model attributes you need
		return "set-goal"; // This should point to your Thymeleaf template
	}

	@PostMapping("/set-goal")
	public String setDonationGoal(@RequestParam BigDecimal amount, RedirectAttributes redirectAttributes) {
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			redirectAttributes.addFlashAttribute("error", "The goal amount must be positive.");
			return "redirect:/donations/set-goal"; // Redirect back to the set goal page
		}
		donationGoalService.setGoal(amount);
		redirectAttributes.addFlashAttribute("message", "Donation goal set successfully!");
		return "redirect:/donations/donation-progress"; // Redirect to the progress page
	}

}
