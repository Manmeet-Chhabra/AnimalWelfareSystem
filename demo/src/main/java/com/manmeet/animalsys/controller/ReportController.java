package com.manmeet.animalsys.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.manmeet.animalsys.entity.AdoptionStatus;
import com.manmeet.animalsys.entity.Animal;
import com.manmeet.animalsys.entity.Attachment;
import com.manmeet.animalsys.entity.Report;
import com.manmeet.animalsys.entity.ReportType;
import com.manmeet.animalsys.entity.User;
import com.manmeet.animalsys.repos.AnimalRepository;
import com.manmeet.animalsys.repos.AttachmentRepository;
import com.manmeet.animalsys.service.AnimalService;
import com.manmeet.animalsys.service.ReportService;
import com.manmeet.animalsys.service.UserService;

@Controller
@RequestMapping("/reports")
public class ReportController {

	@Autowired
	private ReportService reportService;

	@Autowired
	private AnimalService animalService;
	
	@Autowired
	private AttachmentRepository attachmentRepository;
	
	@Autowired
	private AnimalRepository animalRepository;

	@Autowired
	private UserService userService; // Inject UserService for user retrieval

	private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

	
	// Allow all authenticated users to view the report form
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/new")
	public String showReportForm(Model model) {
		model.addAttribute("report", new Report());
		model.addAttribute("types", ReportType.values()); // Pass ReportType enum values to the form
		return "report-form";
	}

	@PreAuthorize("hasRole('USER')")
	@PostMapping("/submit")
	public String submitReport(@ModelAttribute Report report, @RequestParam("file") MultipartFile[] files,
	        Principal principal) {
	    // Get the logged-in user by email
	    User user = userService.findByEmail(principal.getName());

	    if (user == null) {
	        // Log and handle case where user is not found
	        logger.warn("User not found: {}", principal.getName());
	        return "redirect:/error-page"; // Redirect to your error page
	    }

	    // Set the reporter
	    report.setReportedBy(user);

	    // Create a new Animal record with "Unknown" details
	    Animal newAnimal = new Animal();
	    newAnimal.setName("Unknown");
	    newAnimal.setType("Unknown");
	    newAnimal.setHealthStatus("Unknown");
	    newAnimal.setAdoptionStatus(AdoptionStatus.NOT_AVAILABLE);

	    // Save the new Animal record first
	    newAnimal = animalRepository.save(newAnimal); // Persist the animal and get the saved entity

	    // Set the newly created animal in the report
	    report.setAnimal(newAnimal);
	    report.setStatus("PENDING"); // Set default status for the report

	    // Save the report
	    Report savedReport = reportService.submitReport(report);
	    System.out.println("Saved Report ID: " + savedReport.getId()); // Log the ID

	    // Handle file uploads (attachments)
	    if (files != null && files.length > 0) {
	        saveAttachments(savedReport, files);
	    }

	    return "redirect:/reports/history"; // Redirect to user's report history
	}


	

	@PostMapping("/animals/check-or-create/{animalName}")
	public ResponseEntity<Void> checkOrCreateAnimal(@PathVariable String animalName) {
	    Animal existingAnimal = animalRepository.findByName(animalName);
	    if (existingAnimal == null) {
	        Animal newAnimal = new Animal();
	        newAnimal.setName(animalName);
	        animalRepository.save(newAnimal);
	    }
	    return ResponseEntity.ok().build();
	}


	// Allow only users with the ROLE_USER to view their report history
	@GetMapping("/history")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('STAFF')")
	public String viewReportHistory(Model model, Principal principal) {
	    User user = userService.findByEmail(principal.getName());
	    
	    if (user == null) {
	        System.err.println("User not found for email: " + principal.getName());
	        return "redirect:/error-page";
	    }

	    System.out.println("User ID: " + user.getId() + ", Username: " + user.getEmail());

	    List<Report> reports = reportService.getReportsByUser(user);
	    
	    if (reports == null || reports.isEmpty()) {
	        System.out.println("No reports found for user ID: " + user.getId());
	    } else {
	        reports.forEach(report -> System.out.println("Report ID: " + report.getId() + ", Date: " + report.getReportDate()));
	    }

	    model.addAttribute("reports", reports);
	    return "report-history";
	}



	// Allow all authenticated users to view report details
	@GetMapping("/view/{id}")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('STAFF')") // Adjust as necessary
	public String viewReportDetails(@PathVariable Long id, Model model) {
		Optional<Report> reportOptional = reportService.findById(id); // You need to add this method in ReportService
		if (!reportOptional.isPresent()) {
			// Handle the case where the report is not found
			model.addAttribute("errorMessage", "Report not found");
			return "error-page"; // You can redirect to a dedicated error page or return to a list
		}

		Report report = reportOptional.get(); // Get the report safely
		model.addAttribute("report", report);
		model.addAttribute("attachments", reportService.getAttachments(id));
		return "report-details";
	}
	
	
	
	@GetMapping("/{reportId}/attachment/{attachmentId}")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<Resource> viewAttachment(@PathVariable Long reportId, @PathVariable Long attachmentId) {
	    // Fetch the attachment based on reportId and attachmentId
	    Attachment attachment = reportService.findAttachmentByIdAndReportId(attachmentId, reportId);
	    if (attachment == null) {
	        return ResponseEntity.notFound().build();
	    }

	    // Load the file as a Resource
	    Resource resource = new ByteArrayResource(attachment.getData());

	    return ResponseEntity.ok()
	            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + attachment.getFileName() + "\"")
	            .contentType(MediaType.parseMediaType(attachment.getFileType()))
	            .body(resource);
	}
	
	
	
	private void saveAttachments(Report report, MultipartFile[] files) {
		for (MultipartFile file : files) {
			if (!file.isEmpty()) {
				try {
					Attachment attachment = new Attachment();
					attachment.setFileName(file.getOriginalFilename());
					attachment.setFileType(file.getContentType());
					attachment.setData(file.getBytes());
					attachment.setReport(report);
					attachmentRepository.save(attachment);
				} catch (Exception e) {
					// Handle the error appropriately (log it, notify user, etc.)
					System.err.println("Error saving attachment: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	
	@GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public String viewAllReports(Model model) {
        List<Report> reports = reportService.findAllReports();
        model.addAttribute("reports", reports);
        return "admin-reports"; // Thymeleaf view for displaying reports
    }

	@PostMapping("/approve/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public String approveReport(@PathVariable Long id) {
	    reportService.approveReport(id); // Implement this method in ReportService
	    return "redirect:/reports/pending"; // Redirect back to reports page
	}
	

	@PostMapping("/reject/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public String rejectReport(@PathVariable Long id, @RequestParam String rejectionReason) {
	    reportService.rejectReport(id, rejectionReason); // Update this method in ReportService
	    return "redirect:/reports/pending"; // Redirect back to reports page
	}

	// Method to show pending reports
    @GetMapping("/pending")
    public String getPendingReports(Model model) {
        List<Report> pendingReports = reportService.getPendingReports();
        model.addAttribute("reports", pendingReports);
        return "admin-reports-pending"; // Returns the pending-reports.html template
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public String searchReports(@RequestParam(required = false) String description,
                                 @RequestParam(required = false) String type,
                                 @RequestParam(required = false) String status,
                                 Model model) {
        List<Report> searchResults = reportService.searchReports(description, type, status); // Ensure this method is implemented correctly in ReportService
        model.addAttribute("reports", searchResults);
        
        // Add these attributes back to maintain the search state in the form
        model.addAttribute("description", description);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedStatus", status);

        return "admin-reports"; // Returns the same view with search results
    }

	
}
