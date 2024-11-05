package com.manmeet.animalsys.controller;

import java.time.LocalDate;
import java.util.List;

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
import com.manmeet.animalsys.service.NotificationService;
import com.manmeet.animalsys.service.UserService;

@Controller
@RequestMapping("/donations")
public class DonationController {

	@Autowired
	private DonationService donationService;

	@Autowired
	private UserService userService;

	@Autowired
	private NotificationService notificationService;

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
			DonationType.valueOf(donationType); // This will throw an IllegalArgumentException if the type is
												// unsupported
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

			// Send confirmation email to the user
			String userEmail = donation.getUser().getEmail();
			String userSubject = "Thank You for Your Donation!";
			String userBody = String.format("Dear %s,\n\n"
					+ "Thank you for your generous donation of %s towards our cause. Your support makes a significant difference in the lives of animals in need.\n\n"
					+ "If you have any questions or would like to learn more about how your donation is being used, please do not hesitate to reach out.\n\n"
					+ "Best regards,\n" + "Animal Welfare Team", donation.getUser().getName(), donation.getAmount());
			notificationService.sendEmail(userEmail, userSubject, userBody);

			// Fetch admin email using findByRole method
			List<User> admins = userService.findByRole("ROLE_ADMIN");
			String adminEmail;

			// Check if any admins were found
			if (!admins.isEmpty()) {
				adminEmail = admins.get(0).getEmail(); // Get the first admin's email
			} else {
				adminEmail = "sbp.manmeet@gmail.com"; // Fallback to hardcoded admin email
			}

			// Notify admin of the new donation
			String adminSubject = "New Donation Received";
			String adminBody = String.format("Dear Admin,\n\n" + "A new donation has been successfully recorded:\n\n"
					+ "Donor: %s\n" + "Amount: %s\n" + "Donation Date: %s\n" + "Type: %s\n\n"
					+ "Thank you for your attention to this matter.\n\n" + "Best regards,\n" + "Animal Welfare Team",
					donation.getUser().getName(), donation.getAmount(), donation.getDate(), donation.getDonationType());
			notificationService.sendEmail(adminEmail, adminSubject, adminBody);

			redirectAttributes.addFlashAttribute("message",
					"Donation saved successfully! Thank you for your contribution.");
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
	public String deleteDonation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
	    // Fetch the donation before deleting
	    Donation donation = donationService.findById(id);  // Fetch donation by ID

	    // Check if the donation exists and proceed with deletion
	    if (donation != null) {
	        // Proceed to delete the donation
	        donationService.deleteDonation(id);

	        // Fetch admin email using findByRole method
	        List<User> admins = userService.findByRole("ROLE_ADMIN");
	        String adminEmail;

	        // Check if any admins were found
	        if (!admins.isEmpty()) {
	            adminEmail = admins.get(0).getEmail(); // Get the first admin's email
	        } else {
	            adminEmail = "sbp.manmeet@gmail.com"; // Fallback to hardcoded admin email
	        }

	        // Notify admin of the deleted donation
	        String adminSubject = "Donation Deleted";
	        String adminBody = String.format(
	                "Dear Admin,\n\n"
	                        + "Please be informed that the following donation has been deleted from the system:\n\n"
	                        + "Donor: %s\n" + "Amount: %s\n" + "Donation Date: %s\n" + "Type: %s\n\n"
	                        + "If you have any questions or require further details, please do not hesitate to ask.\n\n"
	                        + "Best regards,\n" + "Animal Welfare Team",
	                donation.getUser().getName(), donation.getAmount(), donation.getDate(), donation.getDonationType());
	        notificationService.sendEmail(adminEmail, adminSubject, adminBody);

	        // Notify the donor about the deletion
	        String donorEmail = donation.getUser().getEmail(); // Get donor's email
	        String donorSubject = "Donation Deletion Notification";
	        String donorBody = String.format("Dear %s,\n\n"
	                + "We would like to inform you that your donation of %s made on %s has been deleted from our records.\n\n"
	                + "If you believe this is a mistake or if you have any questions, please contact us at your earliest convenience.\n\n"
	                + "Thank you for your understanding.\n\n" + "Best regards,\n" + "Animal Welfare Team",
	                donation.getUser().getName(), donation.getAmount(), donation.getDate());
	        notificationService.sendEmail(donorEmail, donorSubject, donorBody);

	        // Redirect to the donation list for admin
	        return "redirect:/donations/admin"; 
	    } else {
	        // Donation not found, handle the error
	        redirectAttributes.addFlashAttribute("error", "Donation not found.");
	        return "redirect:/donations/admin";
	    }
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
	public String saveRecurringDonation(@ModelAttribute Donation donation,
			@RequestParam("recurring") boolean recurring) {
		donation.setDate(LocalDate.now()); // Set the current date for the donation
		if (recurring) {
			donationService.scheduleMonthlyDonation(donation); // Schedule for monthly recurrence
		} else {
			donationService.saveDonation(donation); // Save one-time donation
		}
		return "thank-you"; // Redirect to thank you page
	}
}
