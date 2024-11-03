package com.manmeet.animalsys.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.manmeet.animalsys.dto.AdoptionReportDto;
import com.manmeet.animalsys.dto.AnimalCountDto;
import com.manmeet.animalsys.dto.AnimalReportDto;
import com.manmeet.animalsys.dto.DonationReportDto;
import com.manmeet.animalsys.dto.ReportSummaryDto;
import com.manmeet.animalsys.dto.ShelterCapacityDto;


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
	
	
	
	
}
