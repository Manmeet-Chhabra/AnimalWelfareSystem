package com.manmeet.animalsys.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.manmeet.animalsys.dto.AdoptionReportDto;
import com.manmeet.animalsys.dto.AnimalCountDto;
import com.manmeet.animalsys.dto.AnimalReportDto;
import com.manmeet.animalsys.dto.DonationReportDto;
import com.manmeet.animalsys.dto.ReportSummaryDto;
import com.manmeet.animalsys.dto.ShelterCapacityDto;
import com.manmeet.animalsys.service.GenerateReportService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
public class GenerateReportController {

	@Autowired
	private GenerateReportService generateReportService;

	private static final Logger logger = LoggerFactory.getLogger(GenerateReportController.class);

	// Endpoint for Animal Type Distribution Report
	@GetMapping("/reports/animal-distribution")
	public String getAnimalDistribution(Model model) {
		List<AnimalCountDto> animalDistribution = generateReportService.getAnimalTypeDistribution();
		model.addAttribute("animalDistribution", animalDistribution);
		return "reports/animal-distribution";
	}

	// New endpoint for Animal Reports
	@GetMapping("/reports/animal-reports")
	public String getAnimalReports(Model model) {
		List<AnimalReportDto> animalReports = generateReportService.getAllAnimalsReport();
		model.addAttribute("animalReports", animalReports);
		return "reports/animal-reports"; // Thymeleaf view name for animal reports
	}

	@GetMapping("/reports/adoption-trends")
	public String getAdoptionTrends(@RequestParam(value = "startDate", required = false) LocalDate startDate,
			@RequestParam(value = "endDate", required = false) LocalDate endDate, Model model) {

		List<AdoptionReportDto> adoptionTrends = new ArrayList<>();

		if (startDate != null && endDate != null) {
			if (!startDate.isBefore(endDate)) {
				model.addAttribute("error", "Start date must be before end date.");
			} else {
				adoptionTrends = generateReportService.getAdoptionTrends(startDate, endDate);
				model.addAttribute("message", "Adoption trends retrieved successfully.");
			}
		} else {
			model.addAttribute("message", "Please enter a date range to view adoption trends.");
		}

		model.addAttribute("adoptionTrends", adoptionTrends);

		return "reports/adoption-trends"; // The Thymeleaf template to display the results
	}

	// Endpoint for Shelter Capacity Report
	@GetMapping("/reports/shelter-capacity")
	public String getShelterCapacity(Model model) {
		List<ShelterCapacityDto> capacityList = generateReportService.getShelterCapacity();
		model.addAttribute("shelterCapacity", capacityList);
		return "reports/shelter-capacity"; // Thymeleaf view for shelter capacity
	}

	// Endpoint for Donation Summary Report
	@GetMapping("/reports/donation-summary")
	public String getDonationSummary(@RequestParam(value = "startDate", required = false) LocalDate startDate,
			@RequestParam(value = "endDate", required = false) LocalDate endDate, Model model) {

		List<DonationReportDto> donationSummary = new ArrayList<>();

		if (startDate != null && endDate != null) {
			donationSummary = generateReportService.getDonationSummary(startDate, endDate);
		} else {
			model.addAttribute("message", "Please enter a date range to view donation summary.");
		}

		model.addAttribute("donationSummary", donationSummary);

		return "reports/donation-summary"; // The Thymeleaf template for the donation summary
	}

	// Endpoint for displaying Incident Reports
	@GetMapping("/reports/incident-reports")
	public String getIncidentReports(@RequestParam(value = "startDate", required = false) LocalDate startDate,
			@RequestParam(value = "endDate", required = false) LocalDate endDate, Model model) {

		List<ReportSummaryDto> incidentReports = new ArrayList<>();

		if (startDate != null && endDate != null) {
			incidentReports = generateReportService.getIncidentReports(startDate, endDate);
			model.addAttribute("incidentReports", incidentReports);
		} else {
			model.addAttribute("message", "Please enter a date range to view incident reports.");
		}

		return "reports/incident-reports"; // Thymeleaf template for the incident reports
	}

	// Endpoint for Total Adoptions Count
	@GetMapping("/reports/total-adoptions")
	public String getTotalAdoptions(Model model) {
		long totalAdoptions = generateReportService.countTotalAdoptions();
		List<AdoptionReportDto> adoptionTrends = generateReportService.getAdoptionTrends(LocalDate.now().minusYears(1),
				LocalDate.now());

		model.addAttribute("totalAdoptions", totalAdoptions);
		model.addAttribute("adoptionTrends", adoptionTrends != null ? adoptionTrends : new ArrayList<>());

		return "reports/total-adoptions"; // Thymeleaf view for total adoptions
	}

	// Endpoint for Total Donations Count
	@GetMapping("/reports/total-donations")
	public String getTotalDonations(Model model) {
		long totalDonationsCount = generateReportService.countTotalDonations();
		LocalDate startDate = LocalDate.now().minusYears(1);
		LocalDate endDate = LocalDate.now();
		List<DonationReportDto> donationSummary = generateReportService.getDonationSummary(startDate, endDate);

		model.addAttribute("totalDonations", totalDonationsCount);
		model.addAttribute("donationSummary", donationSummary != null ? donationSummary : new ArrayList<>());

		return "reports/total-donations"; // Thymeleaf view for total donations
	}

	@GetMapping("/reports/animal-health-status")
	public String getAnimalHealthStatus(@RequestParam(value = "healthStatus", required = false) String healthStatus,
			Model model) {
		List<AnimalReportDto> filteredAnimals;

		logger.debug("Received healthStatus: {}", healthStatus);

		if (healthStatus != null && !healthStatus.isEmpty()) {
			filteredAnimals = generateReportService.getAnimalsByHealthStatus(healthStatus);
			logger.debug("Filtered animals by health status '{}': {}", healthStatus, filteredAnimals.size());
		} else {
			filteredAnimals = generateReportService.getAllAnimalsReport();
			logger.debug("Retrieved all animals: {}", filteredAnimals.size());
		}

		if (filteredAnimals.isEmpty()) {
			model.addAttribute("message", "No animals found for the selected health status.");
		}

		model.addAttribute("filteredAnimals", filteredAnimals);
		return "reports/animal-health-status"; // Thymeleaf view name
	}

//----------------------------------------------------------------------------------------------------

	// Endpoint to download Animal Distribution Report as PDF
	@GetMapping("/reports/animal-distribution/download/pdf")
	public void downloadAnimalDistributionPdf(HttpServletResponse response) throws IOException {
		// Fetch the animal type distribution data
		List<AnimalCountDto> animalDistribution = generateReportService.getAnimalTypeDistribution();

		// Generate the PDF using the fetched data
		generateReportService.generatePdfForAnimalDistribution(animalDistribution, response);
	}

	// Endpoint to download Donation Summary Report as PDF
	@GetMapping("/reports/donation-summary/download/pdf")
	public void downloadDonationSummaryPdf(@RequestParam(value = "startDate", required = false) LocalDate startDate,
			@RequestParam(value = "endDate", required = false) LocalDate endDate, HttpServletResponse response)
			throws IOException {

		// Set default values if dates are not provided
		if (startDate == null) {
			startDate = LocalDate.MIN; // Minimum date possible
		}
		if (endDate == null) {
			endDate = LocalDate.now(); // Today's date
		}

		// Fetch the donation summary for the specified or default date range
		List<DonationReportDto> donationSummary = generateReportService.getDonationSummary(startDate, endDate);

		// Calculate total donations count
		long totalDonationsCount = donationSummary.size();

		// Generate the PDF with the filtered data
		generateReportService.generatePdfForDonationSummary(donationSummary, totalDonationsCount, response);
	}

	// Endpoint to download Adoption Trends Report as PDF
	@GetMapping("/reports/adoption-trends/download/pdf")
	public void downloadAdoptionTrendsPdf(@RequestParam(value = "startDate", required = false) LocalDate startDate,
			@RequestParam(value = "endDate", required = false) LocalDate endDate, HttpServletResponse response)
			throws IOException {

		// Set default values if dates are not provided
		if (startDate == null) {
			startDate = LocalDate.MIN; // Default to the earliest possible date
		}
		if (endDate == null) {
			endDate = LocalDate.now(); // Default to today's date
		}

		// Fetch the adoption trends for the specified or default date range
		List<AdoptionReportDto> adoptionTrends = generateReportService.getAdoptionTrends(startDate, endDate);

		// Calculate total adoptions count
		long totalAdoptions = adoptionTrends.size();

		// Generate the PDF with the filtered data
		generateReportService.generatePdfForAdoptionTrends(totalAdoptions, adoptionTrends, response);
	}

	// Endpoint to download Animal Distribution Report as Excel
	@GetMapping("/reports/animal-distribution/download/excel")
	public void downloadAnimalDistributionExcel(HttpServletResponse response) throws IOException {
		List<AnimalCountDto> animalDistribution = generateReportService.getAnimalTypeDistribution();
		generateReportService.generateExcelForAnimalDistribution(animalDistribution, response);
	}

	// Endpoint to download Adoption Trends Report as Excel
	@GetMapping("/reports/adoption-trends/download/excel")
	public void downloadAdoptionTrendsExcel(@RequestParam(value = "startDate", required = false) LocalDate startDate,
			@RequestParam(value = "endDate", required = false) LocalDate endDate, HttpServletResponse response)
			throws IOException {

		// Set default values if dates are not provided
		if (startDate == null) {
			startDate = LocalDate.MIN; // Default to the earliest possible date
		}
		if (endDate == null) {
			endDate = LocalDate.now(); // Default to today's date
		}

		// Fetch the adoption trends for the specified or default date range
		List<AdoptionReportDto> adoptionTrends = generateReportService.getAdoptionTrends(startDate, endDate);

		// Calculate total adoptions count
		long totalAdoptions = adoptionTrends.size();

		// Generate the PDF with the filtered data
		generateReportService.generateExcelForAdoptionTrends(totalAdoptions, adoptionTrends, response);
	}

	// Endpoint to download Animal Reports as Excel
	@GetMapping("/reports/animal-reports/download/excel")
	public void downloadAnimalReportsExcel(HttpServletResponse response) throws IOException {
		// Fetch all animal reports from the service
		List<AnimalReportDto> animalReports = generateReportService.getAllAnimalsReport();

		// Generate the PDF using the fetched animal reports
		generateReportService.generateExcelForAnimalReports(animalReports, response);
	}

	// Endpoint to download Donation Summary Report as Excel
	@GetMapping("/reports/donation-summary/download/excel")

	public void downloadDonationSummaryExcel(@RequestParam(value = "startDate", required = false) LocalDate startDate,
			@RequestParam(value = "endDate", required = false) LocalDate endDate, HttpServletResponse response)
			throws IOException {

		// Set default values if dates are not provided
		if (startDate == null) {
			startDate = LocalDate.MIN; // Minimum date possible
		}
		if (endDate == null) {
			endDate = LocalDate.now(); // Today's date
		}

		// Fetch the donation summary for the specified or default date range
		List<DonationReportDto> donationSummary = generateReportService.getDonationSummary(startDate, endDate);

		// Calculate total donations count
		long totalDonationsCount = donationSummary.size();

		// Generate the PDF with the filtered data
		generateReportService.generateExcelForDonationSummary(donationSummary, totalDonationsCount, response);
	}

	// Endpoint to download Animal Reports as PDF
	@GetMapping("/reports/animal-reports/download/pdf")
	public void downloadAnimalReportsPdf(HttpServletResponse response) throws IOException {
		// Fetch all animal reports from the service
		List<AnimalReportDto> animalReports = generateReportService.getAllAnimalsReport();

		// Generate the PDF using the fetched animal reports
		generateReportService.generatePdfForAnimalReports(animalReports, response);
	}

	// Endpoint to download Animal Health Status Report as PDF
	@GetMapping("/reports/animal-health-status/download/pdf")
	public void downloadAnimalHealthStatusPdf(@RequestParam(required = false) String healthStatus,
			HttpServletResponse response) throws IOException {
		List<AnimalReportDto> animals;

		if (healthStatus != null && !healthStatus.isEmpty()) {
			animals = generateReportService.getAnimalsByHealthStatus(healthStatus);
		} else {
			animals = generateReportService.getAllAnimalsReport(); // Fetch all if no filter is applied
		}

		generateReportService.generatePdfForAnimalHealthStatus(animals, response);
	}

	// Similar logic for Excel download
	@GetMapping("/reports/animal-health-status/download/excel")
	public void downloadAnimalHealthStatusExcel(@RequestParam(required = false) String healthStatus,
			HttpServletResponse response) throws IOException {
		List<AnimalReportDto> animals;

		// Check if a health status filter is applied
		if (healthStatus != null && !healthStatus.isEmpty()) {
			animals = generateReportService.getAnimalsByHealthStatus(healthStatus);
		} else {
			animals = generateReportService.getAllAnimalsReport(); // Fetch all if no filter is applied
		}

		// Call the service method to generate the Excel file
		generateReportService.generateExcelForAnimalHealthStatus(animals, response);
	}

	// Endpoint to download Incident Report as PDF
	@GetMapping("/reports/incident-reports/export/pdf")
	public void downloadIncidentReportSummaryPdf(
			@RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			HttpServletResponse response) throws IOException {

		// Set default values if dates are not provided
		if (startDate == null) {
			startDate = LocalDate.MIN; // Minimum date possible
		}
		if (endDate == null) {
			endDate = LocalDate.now(); // Today's date
		}

		// Validate date parameters
		if (startDate.isAfter(endDate)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Start date must be before end date.");
			return;
		}

		// Generate incident reports
		List<ReportSummaryDto> incidentReports = generateReportService.getIncidentReports(startDate, endDate);

		if (incidentReports.isEmpty()) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "No reports found for the given date range.");
			return;
		}

		generateReportService.generatePdfForReportSummary(incidentReports, response);
	}

	// Endpoint to download Incident Report as Excel
	@GetMapping("/reports/incident-reports/export/excel")
	public void downloadIncidentReportSummaryExcel(
			@RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			HttpServletResponse response) throws IOException {

		// Set default values if dates are not provided
		if (startDate == null) {
			startDate = LocalDate.MIN; // Minimum date possible
		}
		if (endDate == null) {
			endDate = LocalDate.now(); // Today's date
		}

		// Validate date parameters
		if (startDate.isAfter(endDate)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Start date must be before end date.");
			return;
		}

		// Generate incident reports
		List<ReportSummaryDto> incidentReports = generateReportService.getIncidentReports(startDate, endDate);

		if (incidentReports.isEmpty()) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "No reports found for the given date range.");
			return;
		}

		generateReportService.generateExcelForReportSummary(incidentReports, response);
	}

	// Endpoint to download Shelter Capacity Report as PDF
	@GetMapping("/reports/shelter-capacity/export/pdf")
	public void downloadShelterCapacityReportPdf(HttpServletResponse response) throws IOException {
		// Fetch the shelter capacity data
		List<ShelterCapacityDto> shelterCapacity = generateReportService.getShelterCapacity();

		// Generate the PDF report
		generateReportService.generatePdfForShelterCapacity(shelterCapacity, response);
	}

	// Endpoint to download Shelter Capacity Report as Excel
	@GetMapping("/reports/shelter-capacity/export/excel")
	public void downloadShelterCapacityReportExcel(HttpServletResponse response) throws IOException {
		// Fetch the shelter capacity data
		List<ShelterCapacityDto> shelterCapacity = generateReportService.getShelterCapacity();

		// Generate the PDF report
		generateReportService.generateExcelForShelterCapacity(shelterCapacity, response);
	}

	// Endpoint to download Total Donations Report as PDF
	@GetMapping("/reports/donations/export/pdf")
	public void downloadTotalDonationsReportPdf(
			@RequestParam(value = "startDate", required = false) LocalDate startDate,
			@RequestParam(value = "endDate", required = false) LocalDate endDate, HttpServletResponse response)
			throws IOException {

		// Count total donations
		long totalDonationsCount = generateReportService.countTotalDonations();

		// Fetch donation summary details using the provided date range
		List<DonationReportDto> donationSummary = generateReportService.getDonationSummary(startDate, endDate);

		// Generate the PDF report
		generateReportService.generatePdfForTotalDonations(totalDonationsCount, donationSummary, response);
	}

	// Endpoint to download Total Donations Report as Excel
	@GetMapping("/reports/donations/export/excel")
	public void downloadTotalDonationsReportExcel(
			@RequestParam(value = "startDate", required = false) LocalDate startDate,
			@RequestParam(value = "endDate", required = false) LocalDate endDate, HttpServletResponse response)
			throws IOException {

		// Count total donations
		long totalDonationsCount = generateReportService.countTotalDonations();

		// Fetch donation summary details using the provided date range
		List<DonationReportDto> donationSummary = generateReportService.getDonationSummary(startDate, endDate);

		// Generate the PDF report
		generateReportService.generateExcelForTotalDonations(totalDonationsCount, donationSummary, response);
	}

	// Endpoint to download Total Adoptions Report as PDF
	@GetMapping("/reports/adoptions/export/pdf")
	public void downloadTotalAdoptionsReportPdf(
			@RequestParam(value = "startDate", required = false) LocalDate startDate,
			@RequestParam(value = "endDate", required = false) LocalDate endDate, HttpServletResponse response)
			throws IOException {

		// Count total adoptions
		long totalAdoptionsCount = generateReportService.countTotalAdoptions();

		// Fetch adoption trends based on the date range
		List<AdoptionReportDto> adoptionTrends = generateReportService.getAdoptionTrends(startDate, endDate);

		// Generate the PDF report
		generateReportService.generatePdfForTotalAdoptions(totalAdoptionsCount, adoptionTrends, response);
	}

	// Endpoint to download Total Adoptions Report as Excel
	@GetMapping("/reports/adoptions/export/excel")
	public void downloadTotalAdoptionsReportExcel(
			@RequestParam(value = "startDate", required = false) LocalDate startDate,
			@RequestParam(value = "endDate", required = false) LocalDate endDate, HttpServletResponse response)
			throws IOException {

		// Count total adoptions
		long totalAdoptionsCount = generateReportService.countTotalAdoptions();

		// Fetch adoption trends based on the date range
		List<AdoptionReportDto> adoptionTrends = generateReportService.getAdoptionTrends(startDate, endDate);

		// Generate the PDF report
		generateReportService.generateExcelForTotalAdoptions(totalAdoptionsCount, adoptionTrends, response);
	}

}
