package com.manmeet.animalsys.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.manmeet.animalsys.dto.AdoptionReportDto;
import com.manmeet.animalsys.dto.AnimalCountDto;
import com.manmeet.animalsys.dto.AnimalReportDto;
import com.manmeet.animalsys.dto.DonationReportDto;
import com.manmeet.animalsys.dto.ReportSummaryDto;
import com.manmeet.animalsys.dto.ShelterCapacityDto;
import com.manmeet.animalsys.entity.Adoption;
import com.manmeet.animalsys.entity.Animal;
import com.manmeet.animalsys.entity.Donation;
import com.manmeet.animalsys.entity.Report;
import com.manmeet.animalsys.entity.Shelter;
import com.manmeet.animalsys.repos.AdoptionRepository;
import com.manmeet.animalsys.repos.AnimalRepository;
import com.manmeet.animalsys.repos.DonationRepository;
import com.manmeet.animalsys.repos.ReportRepository;
import com.manmeet.animalsys.repos.ShelterRepository;
import com.manmeet.animalsys.service.GenerateReportService;

@Service
public class GenerateReportServiceImpl implements GenerateReportService {

    @Autowired
    private AnimalRepository animalRepository;
    @Autowired
    private AdoptionRepository adoptionRepository;
    @Autowired
    private DonationRepository donationRepository;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private ShelterRepository shelterRepository;

    //@Override
    //public Map<String, Long> getAnimalTypeDistribution() {
    //    return animalRepository.countAnimalsByType();
    //}
    
    @Override
    public List<AnimalCountDto> getAnimalTypeDistribution() {
        List<AnimalCountDto> result = animalRepository.countAnimalsByType();
        System.out.println("Animal Distribution Result: " + result);
        return result;
    }

    @Override
    public List<AnimalReportDto> getAnimalsByHealthStatus(String healthStatus) {
        List<Animal> animals = animalRepository.findByHealthStatus(healthStatus);
        // Convert to DTO
        return convertToDto(animals); // This will convert the list of Animal to AnimalReportDto
    }

    @Override
    public long countAnimalsByHealthStatus(String healthStatus) {
        return animalRepository.findByHealthStatus(healthStatus).size(); // You could also use a dedicated method in your repository
    }

    private List<AnimalReportDto> convertToDto(List<Animal> animals) {
        return animals.stream()
                .map(animal -> new AnimalReportDto(
                        animal.getName(), // Required field
                        animal.getType(), // Required field
                        animal.getHealthStatus(), // Required field
                        animal.getShelter() != null ? animal.getShelter().getName() : null, // Shelter name, handle null
                        animal.getDoctorAppointment() // Yes/No for appointment
                ))
                .collect(Collectors.toList());
    }

    
    
    @Override
    public List<AnimalReportDto> getAllAnimalsReport() {
        List<Animal> animals = animalRepository.findAll();
        List<AnimalReportDto> animalReports = new ArrayList<>();

        for (Animal animal : animals) {
            AnimalReportDto dto = new AnimalReportDto();
            dto.setName(animal.getName());
            dto.setType(animal.getType());
            dto.setHealthStatus(animal.getHealthStatus());
            dto.setPictureUrl(animal.getPictureUrl());
            dto.setShelterName(animal.getShelter() != null ? animal.getShelter().getName() : "No Shelter Assigned");
            dto.setDoctorAppointment(animal.getDoctorAppointment() != null ? animal.getDoctorAppointment() : "No Appointment"); // Set Yes/No
            dto.setAdoptionStatus(animal.getAdoptionStatus());

            animalReports.add(dto);
        }

        return animalReports;
    }




    
    @Override
    public List<AdoptionReportDto> getAdoptionTrends(LocalDate startDate, LocalDate endDate) {
        List<Adoption> adoptions = adoptionRepository.findByDateRange(startDate, endDate);
        List<AdoptionReportDto> adoptionReport = new ArrayList<>();

        for (Adoption adoption : adoptions) {
            AdoptionReportDto dto = new AdoptionReportDto(
                adoption.getAnimal().getName(),     // Animal Name
                adoption.getUser().getName(),        // Adopter Name
                adoption.getAdoptionDate(),          // Adoption Date
                adoption.getStatus(),                 // Status
                adoption.getRequestDate(),            // Request Date
                adoption.getScore()                   // Score
            );
            adoptionReport.add(dto);
        }

        return adoptionReport;
    }


    @Override
    public long countTotalDonations() {
        return donationRepository.count(); // Assuming you have a method in your repository to count donations
    }

    @Override
    public List<DonationReportDto> getDonationSummary(LocalDate startDate, LocalDate endDate) {
        List<Donation> donations = donationRepository.findByDateRange(startDate, endDate);
        List<DonationReportDto> donationSummary = new ArrayList<>();

        for (Donation donation : donations) {
            Date donationDate = Date.from(donation.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
            donationSummary.add(new DonationReportDto(
                donation.getDonorName(),                   // Donor name from Donation entity
                donation.getAmount(),                      // Amount from Donation entity
                donationDate,                              // Donation date
                donation.getDonationType().name(),        // Donation type as string (assuming you want the name of the enum)
                donation.getQuantity(),                    // Quantity from Donation entity
                donation.isRecurring()                     // Recurring status
            ));
        }

        return donationSummary;
    }

    @Override
    public List<ShelterCapacityDto> getShelterCapacity() {
        List<Shelter> shelters = shelterRepository.findAll();
        List<ShelterCapacityDto> shelterCapacityDtos = new ArrayList<>();

        for (Shelter shelter : shelters) {
            // Check if currentOccupancy is null and set to 0 if it is
            Integer currentOccupancy = shelter.getCurrentOccupancy();
            int occupancy = (currentOccupancy != null) ? currentOccupancy : 0;

            ShelterCapacityDto dto = new ShelterCapacityDto(
                shelter.getName(),
                shelter.getLocation(),
                shelter.getContactDetails(),
                occupancy,  // Use the occupancy variable instead of the original method call
                shelter.getCapacity()
            );
            shelterCapacityDtos.add(dto);
        }

        return shelterCapacityDtos;
    }



 

    
    @Override
    public BigDecimal calculateTotalDonations() {
        BigDecimal total = donationRepository.sumAmount();
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public List<ReportSummaryDto> getIncidentReports(LocalDate startDate, LocalDate endDate) {
        // Convert LocalDate to LocalDateTime for the entire day range
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        // Fetch reports within the date range using LocalDateTime
        List<Report> reports = reportRepository.findByDateRange(startDateTime, endDateTime);
        List<ReportSummaryDto> reportSummaries = new ArrayList<>();

        // Populate ReportSummaryDto objects
        for (Report report : reports) {
            reportSummaries.add(new ReportSummaryDto(
                report.getType().name(),
                report.getStatus(),
                report.getDescription(),
                report.getReportDate(),
                report.getLocation(),
                report.getAddress(),
                report.getRejectionReason()
            ));
        }

        return reportSummaries;
    }


	@Override
	public long countTotalAdoptions() {
	    return adoptionRepository.count(); // Assuming adoptionRepository is correctly set up to count records
	}



 

}
