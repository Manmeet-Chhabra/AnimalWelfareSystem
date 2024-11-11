package com.manmeet.animalsys.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.manmeet.animalsys.dto.UserDto;
import com.manmeet.animalsys.entity.Donation;
import com.manmeet.animalsys.entity.User;
import com.manmeet.animalsys.service.AdoptionService;
import com.manmeet.animalsys.service.AnimalService;
import com.manmeet.animalsys.service.DonationService;
import com.manmeet.animalsys.service.NotificationService;
import com.manmeet.animalsys.service.ReportService;
import com.manmeet.animalsys.service.UserService;

import jakarta.validation.Valid;

@Controller
public class AuthController {

	@Autowired
	private UserService userService;

	@Autowired
	private AnimalService animalService;

	@Autowired
	private AdoptionService adoptionService;

	@Autowired
	private ReportService reportService;

	@Autowired
	private DonationService donationService;

	@Autowired
	private NotificationService notificationService;

	@GetMapping("/index")
	public String home() {
		return "index";
	}

	@GetMapping("/login")
	public String loginForm(Authentication authentication) {
		if (authentication != null && authentication.isAuthenticated()) {
			return "redirect:/dashboard"; // Redirect to the single dashboard for all roles
		}
		return "login"; // Show the login form if not authenticated
	}

	// handler method to show the registration form
	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		// Create a new UserDto and add it to the model
		UserDto userDto = new UserDto();
		model.addAttribute("user", userDto);
		return "register";
	}

	// handler method to process the registration form submission
	@PostMapping("/register/save")
	public String registerUser(@Valid @ModelAttribute("user") UserDto userDto, BindingResult result, Model model) {
		// Check if the email is already registered
		User existingUser = userService.findByEmail(userDto.getEmail());
		if (existingUser != null) {
			result.rejectValue("email", null, "There is already an account registered with this email");
		}

		// If there are validation errors, return the registration form
		if (result.hasErrors()) {
			model.addAttribute("user", userDto);
			return "register";
		}

		// Save the user with the selected role
		userService.saveUser(userDto);

		// Send email to the user
		String userSubject = "Welcome to Animal Welfare System";
		String userBody = String.format(
				"Dear %s %s,\n\nThank you for registering with us! We are thrilled to welcome you to our community dedicated to animal welfare.\n\n"
						+ "If you have any questions or need assistance, feel free to reach out to us.\n\n"
						+ "Best regards,\nAnimal Welfare Team",
				userDto.getFirstName(), userDto.getLastName());
		notificationService.sendEmail(userDto.getEmail(), userSubject, userBody);

		// Fetch admin email using findByRole method
		List<User> admins = userService.findByRole("ROLE_ADMIN");
		String adminEmail;

		// Check if any admins were found
		if (!admins.isEmpty()) {
			adminEmail = admins.get(0).getEmail(); // Get the first admin's email
		} else {
			adminEmail = "sbp.manmeet@gmail.com"; // Fallback to hardcoded admin email
		}

		// Send email to the admin
		String adminSubject = "New User Registration";
		String adminBody = String.format(
				"Dear Admin,\n\nA new user has successfully registered on the platform:\n\n" + "Name: %s %s\n"
						+ "Email: %s\n" + "Role: %s\n\n" + "Please review their account and reach out if necessary.\n\n"
						+ "Best regards,\nAnimal Welfare System",
				userDto.getFirstName(), userDto.getLastName(), userDto.getEmail(), userDto.getRole());
		notificationService.sendEmail(adminEmail, adminSubject, adminBody);

		// Redirect to the registration page with a success message
		return "redirect:/register?success";
	}

	@GetMapping("/users")
	public String listRegisteredUsers(Model model) {
		List<UserDto> users = userService.findAllUsers();
		model.addAttribute("users", users);
		return "users";
	}

	@GetMapping("/dashboard")
	public String dashboard(Model model, Authentication authentication) {
		// Get the roles of the logged-in user
		Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

		// Check the role and pass it to the model
		if (roles.contains("ROLE_ADMIN")) {
			model.addAttribute("role", "admin"); // role as a string, enclosed in single quotes
		} else if (roles.contains("ROLE_USER")) {
			model.addAttribute("role", "user");
		} else if (roles.contains("ROLE_STAFF")) {
			model.addAttribute("role", "staff");
		} else {
			model.addAttribute("role", "guest"); // Default case
		}

		// Fetch the data for the stats
		long totalAnimals = animalService.getTotalAnimals(); // Get the total number of animals
		long totalAdoptions = adoptionService.getTotalAdoptions(); // Get the total number of adoptions
		long totalIncidents = reportService.getTotalIncidents(); // Get the total number of incidents
		// Get recent donors and add them to the model
		List<Donation> recentDonors = donationService.getRecentDonors();

		// Add the stats to the model
		model.addAttribute("totalAnimals", totalAnimals);
		model.addAttribute("totalAdoptions", totalAdoptions);
		model.addAttribute("totalIncidents", totalIncidents);
		model.addAttribute("recentDonors", recentDonors);

		return "dashboard";
	}

}