package com.manmeet.animalsys.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

@Controller
public class GenerateReportController {

    @Autowired
    private GenerateReportService generateReportService;
    

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    
    // Endpoint for Animal Type Distribution Report
    @GetMapping("/reports/animal-distribution")
    public String getAnimalDistribution(Model model) {
        List<AnimalCountDto> animalDistribution = generateReportService.getAnimalTypeDistribution();
        System.out.println("Animal Distribution: " + animalDistribution); // Log output
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
    public String getAdoptionTrends(
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate,
            Model model) {

        List<AdoptionReportDto> adoptionTrends = new ArrayList<>();

        // Validate that startDate and endDate are provided
        if (startDate != null && endDate != null) {
            if (!startDate.isBefore(endDate)) {
                model.addAttribute("error", "Start date must be before end date.");
            } else {
                // Fetch adoption trends based on the provided date range
                adoptionTrends = generateReportService.getAdoptionTrends(startDate, endDate);
                model.addAttribute("message", "Adoption trends retrieved successfully.");
            }
        } else {
            model.addAttribute("message", "Please enter a date range to view adoption trends.");
        }

        // Add the DTO list to the model
        model.addAttribute("adoptionTrends", adoptionTrends);
        
        return "reports/adoption-trends"; // The Thymeleaf template to display the results
    }






    // Endpoint for Shelter Capacity Report
    @GetMapping("/reports/shelter-capacity")
    public String getShelterCapacity(Model model) {
        List<ShelterCapacityDto> capacityList = generateReportService.getShelterCapacity();
        System.out.println("Shelter Capacity List Size: " + capacityList.size()); // Debug output
        model.addAttribute("shelterCapacity", capacityList);
        return "reports/shelter-capacity"; // Thymeleaf view for shelter capacity
    }
    
    

    // Endpoint for Donation Summary Report
    @GetMapping("/reports/donation-summary")
    public String getDonationSummary(
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate,
            Model model) {

        List<DonationReportDto> donationSummary = new ArrayList<>();

        // Check if both startDate and endDate are provided
        if (startDate != null && endDate != null) {
            donationSummary = generateReportService.getDonationSummary(startDate, endDate);
        } else {
            model.addAttribute("message", "Please enter a date range to view donation summary.");
        }

        model.addAttribute("donationSummary", donationSummary);
        
        return "reports/donation-summary"; // The Thymeleaf template for the donation summary
    }


    // Endpoint for Incident Reports
    @GetMapping("/reports/incident-reports")
    public String getIncidentReports(
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate,
            Model model) {

        List<ReportSummaryDto> incidentReports = new ArrayList<>();

        // Check if both startDate and endDate are provided
        if (startDate != null && endDate != null) {
            incidentReports = generateReportService.getIncidentReports(startDate, endDate);
        } else {
            model.addAttribute("message", "Please enter a date range to view incident reports.");
        }

        model.addAttribute("incidentReports", incidentReports);
        
        return "reports/incident-reports"; // The Thymeleaf template for the incident reports
    }



    // Endpoint for Total Adoptions Count
    @GetMapping("/reports/total-adoptions")
    public String getTotalAdoptions(Model model) {
        long totalAdoptions = generateReportService.countTotalAdoptions();
        List<AdoptionReportDto> adoptionTrends = generateReportService.getAdoptionTrends(LocalDate.now().minusYears(1), LocalDate.now()); // Adjust the date range as needed

        // Add attributes to the model
        model.addAttribute("totalAdoptions", totalAdoptions);
        model.addAttribute("adoptionTrends", adoptionTrends != null ? adoptionTrends : new ArrayList<>()); // Avoid null list

        return "reports/total-adoptions"; // Thymeleaf view for total adoptions
    }




    // Endpoint for Total Donations Count
    @GetMapping("/reports/total-donations")
    public String getTotalDonations(Model model) {
        long totalDonationsCount = generateReportService.countTotalDonations(); // Get total count
        LocalDate startDate = LocalDate.now().minusYears(1); // Example: last month
        LocalDate endDate = LocalDate.now(); // Current date
        List<DonationReportDto> donationSummary = generateReportService.getDonationSummary(startDate, endDate); // Fetch donation summary

        // Add attributes to the model
        model.addAttribute("totalDonations", totalDonationsCount);
        model.addAttribute("donationSummary", donationSummary != null ? donationSummary : new ArrayList<>()); // Avoid null list

        return "reports/total-donations"; // Thymeleaf view for total donations
    }

    @GetMapping("/reports/animal-health-status")
    public String getAnimalHealthStatus(@RequestParam(value = "healthStatus", required = false) String healthStatus, Model model) {
        List<AnimalReportDto> filteredAnimals;

        // Log the incoming health status parameter
        logger.debug("Received healthStatus: {}", healthStatus);

        // Check if a health status filter is provided
        if (healthStatus != null && !healthStatus.isEmpty()) {
            filteredAnimals = generateReportService.getAnimalsByHealthStatus(healthStatus);
            logger.debug("Filtered animals by health status '{}': {}", healthStatus, filteredAnimals.size());
        } else {
            filteredAnimals = generateReportService.getAllAnimalsReport(); // Handle the case when no filter is provided
            logger.debug("Retrieved all animals: {}", filteredAnimals.size());
        }

        if (filteredAnimals.isEmpty()) {
            model.addAttribute("message", "No animals found for the selected health status.");
        }

        // Add the filtered animals to the model
        model.addAttribute("filteredAnimals", filteredAnimals);
        return "reports/animal-health-status"; // Thymeleaf view name
    }

    
      
    
}

