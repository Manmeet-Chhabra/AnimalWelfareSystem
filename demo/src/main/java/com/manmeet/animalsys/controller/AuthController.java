package com.manmeet.animalsys.controller;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.manmeet.animalsys.dto.UserDto;
import com.manmeet.animalsys.entity.Role;
import com.manmeet.animalsys.entity.User;
import com.manmeet.animalsys.service.UserService;

import jakarta.validation.Valid;

@Controller
public class AuthController {

	private UserService userService;

	public AuthController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/index")
	public String home() {
		return "index";
	}

	@GetMapping("/login")
	public String loginForm(Authentication authentication) {
		if (authentication != null && authentication.isAuthenticated()) {
			Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
			for (GrantedAuthority authority : authorities) {
				if (authority.getAuthority().equals("ROLE_ADMIN")) {
					return "redirect:/admin-dashboard";
				} else if (authority.getAuthority().equals("ROLE_USER")) {
					return "redirect:/user-dashboard";
				}
			}
		}
		return "login";
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
    public String registerUser(@Valid @ModelAttribute("user") UserDto userDto, 
                               BindingResult result, 
                               Model model) {
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

        // Redirect to the registration page with a success message
        return "redirect:/register?success";
    }



	@GetMapping("/users")
	public String listRegisteredUsers(Model model) {
		List<UserDto> users = userService.findAllUsers();
		model.addAttribute("users", users);
		return "users";
	}

	@GetMapping("/admin-dashboard")
	public String adminDashboard() {
		return "admin-dashboard"; // Points to admin-dashboard.html
	}

	@GetMapping("/user-dashboard")
	public String userDashboard() {
		return "user-dashboard"; // Points to user-dashboard.html
	}
	
	@GetMapping("/staff-dashboard")
	public String staffDashboard() {
		return "staff-dashboard"; // Points to user-dashboard.html
	}
}