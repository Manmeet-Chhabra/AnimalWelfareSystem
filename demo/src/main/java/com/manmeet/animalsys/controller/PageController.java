package com.manmeet.animalsys.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {

	@GetMapping("/about")
	public String about() {
		return "about";
	}

	@GetMapping("/education")
	public String education() {
		return "education";
	}

	@GetMapping("/pet-care-tips")
	public String petCareTips() {
		return "pet-care-tips";
	}

	@GetMapping("/forum")
	public String forum() {
		return "forum";
	}

	@GetMapping("/forum/pet-care")
	public String petCare() {
		return "pet-care-forum";
	}

	@GetMapping("/forum/adoption")
	public String adoption() {
		return "adoption-forum";
	}

	@GetMapping("/forum/volunteering")
	public String volunteering() {
		return "volunteering-forum";
	}

	@GetMapping("/feedback")
	public String feedback() {
		return "feedback";
	}

	@PostMapping("/submit-feedback")
	public String submitFeedback(@RequestParam("name") String name, @RequestParam("email") String email,
			@RequestParam("message") String message) {

		return "feedback-confirmation";
	}
}