package com.manmeet.animalsys.controller;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
import com.manmeet.animalsys.entity.DonationType;
import com.manmeet.animalsys.entity.User;
import com.manmeet.animalsys.service.DonationService;
import com.manmeet.animalsys.service.UserService;

@Controller
@RequestMapping("/donations")
public class DonationController {

	@Autowired
	private DonationService donationService;

	@Autowired
	private UserService userService;

	// Display the main donation page with dropdown
	@GetMapping
	public String showMainPage(Model model) {
		return "main-donation-page"; // Thymeleaf template name for main page(index)
	}

	// Display the donation page for a specific type
    @GetMapping("/{type}")
    public String showDonationPage(@PathVariable("type") String type, Model model) {
        System.out.println("Accessing donation type: " + type); // Debug log
        Logger logger = LoggerFactory.getLogger(DonationController.class);
        logger.info("Requested donation type: {}", type); // Log requested donation type
        
        // Convert type to uppercase
        String donationType = type.toUpperCase();

        // Validate the donation type using an enum
        try {
            DonationType.valueOf(donationType); // This will throw an IllegalArgumentException if the type is unsupported
        } catch (IllegalArgumentException e) {
            logger.warn("Unsupported donation type: {}", donationType);
            return "error-page"; // Return a specific error page template
        }

        // Create a new Donation object and add it to the model
        Donation donation = new Donation(); // Ensure you have an empty Donation object for the form
        donation.setDonationType(DonationType.valueOf(donationType)); // Set the donation type
        model.addAttribute("donation", donation); // Add it to the model for binding in the form

        return "donation-" + type.toLowerCase(); // Return the specific donation type template
    }
	// Display the donation form - allowed for all users
	@GetMapping("/new")
	@PreAuthorize("isAuthenticated()") // Ensure user is authenticated
	public String showDonationForm(Model model) {
		model.addAttribute("donation", new Donation());
		return "donation-form"; // Thymeleaf template name
	}

	@PostMapping("/save")
	@PreAuthorize("isAuthenticated()") // Ensure user is authenticated
	public String saveDonation(@ModelAttribute Donation donation, RedirectAttributes redirectAttributes) {
	    donation.setDate(LocalDate.now()); // Set current date

	    try {
	        donationService.saveDonation(donation);
	        redirectAttributes.addFlashAttribute("message", "Donation saved successfully! Thank you for your contribution.");
	        return "redirect:/donations/thank-you"; // Redirect to thank you page
	    } catch (IllegalArgumentException e) {
	        redirectAttributes.addFlashAttribute("error", e.getMessage());
	        return "redirect:/donations"; // Redirect back to the donations page
	    }
	}
	
	@GetMapping("/thank-you")
	public String showThankYouPage() {
	    return "thank-you"; // Thymeleaf template for thank you page
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

	// ---------------- New Features ----------------

		// 1. Recent Donors (Scrolling List)
	@GetMapping("/recent-donors")
	public String showRecentDonorsPage(Model model) {
	    List<Donation> recentDonors = donationService.getRecentDonations();
	    model.addAttribute("recentDonors", recentDonors);
	    return "recent-donors"; // This should be the name of the Thymeleaf template
	}

		// 2. Donor History (Personal Dashboard)
	@GetMapping("/history")
	@PreAuthorize("isAuthenticated()")
	public String getDonationHistory(Model model) {
	    User currentUser = userService.getCurrentUser(); // Get the current user
	    List<Donation> userDonations = donationService.findDonationsByUser(currentUser); // Pass user object
	    model.addAttribute("donations", userDonations);

	    return "donor-history"; // Thymeleaf template for user donation history
	}


		// 3. One-Time or Monthly Donations
		@PostMapping("/recurring/save")
		@PreAuthorize("isAuthenticated()")
		public String saveRecurringDonation(@ModelAttribute Donation donation, @RequestParam("recurring") boolean recurring) {
			donation.setDate(LocalDate.now()); // Set the current date for the donation
			if (recurring) {
				donationService.scheduleMonthlyDonation(donation); // Schedule for monthly recurrence
			} else {
				donationService.saveDonation(donation); // Save one-time donation
			}
			return "thank-you"; // Redirect to thank you page
		}
	}
	

