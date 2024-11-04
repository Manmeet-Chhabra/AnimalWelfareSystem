package com.manmeet.animalsys.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.manmeet.animalsys.dto.AdoptionReportDto;
import com.manmeet.animalsys.dto.AnimalCountDto;
import com.manmeet.animalsys.dto.AnimalReportDto;
import com.manmeet.animalsys.dto.DonationReportDto;
import com.manmeet.animalsys.dto.ReportSummaryDto;
import com.manmeet.animalsys.dto.ShelterCapacityDto;

import jakarta.servlet.http.HttpServletResponse;


public interface GenerateReportService {

	// Animal Statistics Reports
    List<AnimalCountDto> getAnimalTypeDistribution();
    List<AnimalReportDto> getAnimalsByHealthStatus(String healthStatus);
    List<AnimalReportDto> getAllAnimalsReport();
    long countAnimalsByHealthStatus(String healthStatus);

    // Adoption Reports
    List<AdoptionReportDto> getAdoptionTrends(LocalDate startDate, LocalDate endDate);
    long countTotalAdoptions();

    // Shelter Capacity Report (simplified for now)
    List<ShelterCapacityDto> getShelterCapacity();

    // Donation Reports
    List<DonationReportDto> getDonationSummary(LocalDate startDate, LocalDate endDate);
    BigDecimal calculateTotalDonations();
    

    // Incident/Reporting Summary
    List<ReportSummaryDto> getIncidentReports(LocalDate startDate, LocalDate endDate);
	long countTotalDonations();
	
	
	void generateExcelForTotalAdoptions(long totalAdoptions, List<AdoptionReportDto> adoptionTrends,
			HttpServletResponse response) throws IOException;
	void generatePdfForTotalAdoptions(long totalAdoptions, List<AdoptionReportDto> adoptionTrends,
			HttpServletResponse response) throws IOException;
	
	void generatePdfForTotalDonations(long totalDonationsCount, List<DonationReportDto> donationSummary,
			HttpServletResponse response) throws IOException;
	void generateExcelForTotalDonations(long totalDonationsCount, List<DonationReportDto> donationSummary,
			HttpServletResponse response) throws IOException;
	
	void generateExcelForAnimalHealthStatus(List<AnimalReportDto> filteredAnimals, HttpServletResponse response)
			throws IOException;
	void generatePdfForAnimalHealthStatus(List<AnimalReportDto> filteredAnimals, HttpServletResponse response)
			throws IOException;
	
	void generateExcelForAdoptionTrends(long totalAdoptions, List<AdoptionReportDto> adoptionTrends,
			HttpServletResponse response) throws IOException;
	void generatePdfForAdoptionTrends(long totalAdoptions, List<AdoptionReportDto> adoptionTrends,
			HttpServletResponse response) throws IOException;
	
	void generateExcelForAnimalDistribution(List<AnimalCountDto> animalDistribution, HttpServletResponse response)
			throws IOException;
	void generatePdfForAnimalDistribution(List<AnimalCountDto> animalDistribution, HttpServletResponse response)
			throws IOException;
	
	void generateExcelForShelterCapacity(List<ShelterCapacityDto> shelterCapacity, HttpServletResponse response)
			throws IOException;
	void generatePdfForShelterCapacity(List<ShelterCapacityDto> shelterCapacity, HttpServletResponse response)
			throws IOException;
	
	//void generateExcelForReportSummary(List<ReportSummaryDto> incidentReports, ReportSummaryDto reportSummary,
	//		HttpServletResponse response) throws IOException;
	//void generatePdfForReportSummary(List<ReportSummaryDto> incidentReports, ReportSummaryDto reportSummary,
	//		HttpServletResponse response) throws IOException;
	
	void generatePdfForAnimalReports(List<AnimalReportDto> animalReports, HttpServletResponse response)
			throws IOException;
	void generateExcelForAnimalReports(List<AnimalReportDto> animalReports, HttpServletResponse response)
			throws IOException;
	
	void generatePdfForDonationSummary(List<DonationReportDto> donationSummary, long totalDonationsCount,
			HttpServletResponse response) throws IOException;
	void generateExcelForDonationSummary(List<DonationReportDto> donationSummary, long totalDonationsCount,
			HttpServletResponse response) throws IOException;
	
	void generateExcelForReportSummary(List<ReportSummaryDto> incidentReports, HttpServletResponse response)
			throws IOException;
	void generatePdfForReportSummary(List<ReportSummaryDto> incidentReports, HttpServletResponse response)
			throws IOException;
	
	
	
	
}