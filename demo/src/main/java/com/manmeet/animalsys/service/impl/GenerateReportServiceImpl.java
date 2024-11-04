package com.manmeet.animalsys.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
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

import jakarta.servlet.http.HttpServletResponse;

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

	// @Override
	// public Map<String, Long> getAnimalTypeDistribution() {
	// return animalRepository.countAnimalsByType();
	// }

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
		return animalRepository.findByHealthStatus(healthStatus).size(); // You could also use a dedicated method in
																			// your repository
	}

	@Override
	public List<AnimalReportDto> getAllAnimalsReport() {
	    List<Animal> animals = animalRepository.findAll();
	    return convertToDto(animals);
	}

	/*@Override
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
			dto.setDoctorAppointment(
					animal.getDoctorAppointment() != null ? animal.getDoctorAppointment() : "No Appointment"); // Set
																												// Yes/No
			dto.setAdoptionStatus(animal.getAdoptionStatus());

			animalReports.add(dto);
		}

		return animalReports;
	}*/
	
	private List<AnimalReportDto> convertToDto(List<Animal> animals) {
	    return animals.stream().map(animal -> new AnimalReportDto(
	            animal.getName(), // Required field
	            animal.getType(), // Required field
	            animal.getHealthStatus(), // Required field
	            animal.getShelter() != null ? animal.getShelter().getName() : "No Shelter Assigned", // Shelter name
	            animal.getDoctorAppointment() != null ? animal.getDoctorAppointment() : "No Appointment" // Appointment
	    )).collect(Collectors.toList());
	}

	

	@Override
	public List<AdoptionReportDto> getAdoptionTrends(LocalDate startDate, LocalDate endDate) {
		List<Adoption> adoptions = adoptionRepository.findByDateRange(startDate, endDate);
		List<AdoptionReportDto> adoptionReport = new ArrayList<>();

		for (Adoption adoption : adoptions) {
			AdoptionReportDto dto = new AdoptionReportDto(adoption.getAnimal().getName(), // Animal Name
					adoption.getUser().getName(), // Adopter Name
					adoption.getAdoptionDate(), // Adoption Date
					adoption.getStatus(), // Status
					adoption.getRequestDate(), // Request Date
					adoption.getScore() // Score
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
			donationSummary.add(new DonationReportDto(donation.getDonorName(), // Donor name from Donation entity
					donation.getAmount(), // Amount from Donation entity
					donationDate, // Donation date
					donation.getDonationType().name(), // Donation type as string (assuming you want the name of the
														// enum)
					donation.getQuantity(), // Quantity from Donation entity
					donation.isRecurring() // Recurring status
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

			ShelterCapacityDto dto = new ShelterCapacityDto(shelter.getName(), shelter.getLocation(),
					shelter.getContactDetails(), occupancy, // Use the occupancy variable instead of the original method
															// call
					shelter.getCapacity());
			shelterCapacityDtos.add(dto);
		}

		return shelterCapacityDtos;
	}

	@Override
	public BigDecimal calculateTotalDonations() {
		BigDecimal total = donationRepository.sumAmount();
		return total != null ? total : BigDecimal.ZERO;
	}

	// Fetch incident reports within the date range
	public List<ReportSummaryDto> getIncidentReports(LocalDate startDate, LocalDate endDate) {
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

		List<Report> reports = reportRepository.findByDateRange(startDateTime, endDateTime);
		List<ReportSummaryDto> reportSummaries = new ArrayList<>();

		for (Report report : reports) {
			reportSummaries.add(new ReportSummaryDto(report.getType().name(), report.getStatus(),
					report.getDescription(), report.getReportDate(), report.getLocation(), report.getAddress(),
					report.getRejectionReason()));
		}

		return reportSummaries;
	}

	@Override
	public long countTotalAdoptions() {
		return adoptionRepository.count(); // Assuming adoptionRepository is correctly set up to count records
	}

	// ------------------------------------------------------------------------------------------------------------------------------

	@Override
	public void generatePdfForAnimalReports(List<AnimalReportDto> animalReports, HttpServletResponse response)
			throws IOException {
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=animal_reports.pdf");

		PdfWriter pdfWriter = new PdfWriter(response.getOutputStream());
		PdfDocument pdfDocument = new PdfDocument(pdfWriter);
		Document document = new Document(pdfDocument);

		document.add(new Paragraph("Animal Reports").setTextAlignment(TextAlignment.CENTER).setFontSize(20).setBold()
				.setFontColor(ColorConstants.BLUE));

		Table table = new Table(new float[] { 1, 1, 1, 1, 1, 1 });
		table.setWidth(UnitValue.createPercentValue(100));
		table.addHeaderCell(new Cell().add(new Paragraph("Name").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Type").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Health Status").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Doctor Appointment").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Shelter Name").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Adoption Status").setBold()));

		for (AnimalReportDto report : animalReports) {
			table.addCell(new Cell().add(new Paragraph(report.getName())));
			table.addCell(new Cell().add(new Paragraph(report.getType())));
			table.addCell(new Cell().add(new Paragraph(report.getHealthStatus())));
			table.addCell(new Cell()
					.add(new Paragraph(report.getDoctorAppointment() != null ? report.getDoctorAppointment() : "N/A")));
			table.addCell(new Cell().add(
					new Paragraph(report.getShelterName() != null ? report.getShelterName() : "No Shelter Assigned")));
			table.addCell(new Cell().add(
					new Paragraph(report.getAdoptionStatus() != null ? report.getAdoptionStatus().toString() : "N/A")));
		}

		document.add(table);
		document.close();
	}

	@Override
	public void generateExcelForAnimalReports(List<AnimalReportDto> animalReports, HttpServletResponse response)
			throws IOException {
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=animal_reports.xlsx");

		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Animal Reports");

		Row headerRow = sheet.createRow(0);
		headerRow.createCell(0).setCellValue("Name");
		headerRow.createCell(1).setCellValue("Type");
		headerRow.createCell(2).setCellValue("Health Status");
		headerRow.createCell(3).setCellValue("Doctor Appointment");
		headerRow.createCell(4).setCellValue("Shelter Name");
		headerRow.createCell(5).setCellValue("Adoption Status");

		int rowNum = 1;
		for (AnimalReportDto report : animalReports) {
			Row row = sheet.createRow(rowNum++);
			row.createCell(0).setCellValue(report.getName());
			row.createCell(1).setCellValue(report.getType());
			row.createCell(2).setCellValue(report.getHealthStatus());
			row.createCell(3)
					.setCellValue(report.getDoctorAppointment() != null ? report.getDoctorAppointment() : "N/A");
			row.createCell(4)
					.setCellValue(report.getShelterName() != null ? report.getShelterName() : "No Shelter Assigned");
			row.createCell(5)
					.setCellValue(report.getAdoptionStatus() != null ? report.getAdoptionStatus().toString() : "N/A");
		}

		workbook.write(response.getOutputStream());
		workbook.close();
	}

	@Override
	public void generatePdfForTotalAdoptions(long totalAdoptions, List<AdoptionReportDto> adoptionTrends,
			HttpServletResponse response) throws IOException {
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=total_adoptions_report.pdf");

		PdfWriter pdfWriter = new PdfWriter(response.getOutputStream());
		PdfDocument pdfDocument = new PdfDocument(pdfWriter);
		Document document = new Document(pdfDocument);

		document.add(new Paragraph("Total Adoptions Report").setTextAlignment(TextAlignment.CENTER).setFontSize(20)
				.setBold().setFontColor(ColorConstants.BLUE));

		document.add(new Paragraph("Total Adoptions: " + totalAdoptions).setFontSize(16));

		Table table = new Table(new float[] { 2, 2, 2, 2, 1 });
		table.setWidth(UnitValue.createPercentValue(100));
		table.addHeaderCell(new Cell().add(new Paragraph("Animal Name").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Adopter Name").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Adoption Date").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Status").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Score").setBold()));

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		for (AdoptionReportDto adoption : adoptionTrends) {
			table.addCell(new Cell().add(new Paragraph(adoption.getAnimalName())));
			table.addCell(new Cell().add(new Paragraph(adoption.getAdopterName())));
			table.addCell(new Cell().add(new Paragraph(
					adoption.getAdoptionDate() != null ? dateFormat.format(adoption.getAdoptionDate()) : "")));
			table.addCell(new Cell().add(new Paragraph(adoption.getStatus().toString())));
			table.addCell(new Cell().add(new Paragraph(String.valueOf(adoption.getScore()))));
		}

		document.add(table);
		document.close();
	}

	@Override
	public void generateExcelForTotalAdoptions(long totalAdoptions, List<AdoptionReportDto> adoptionTrends,
			HttpServletResponse response) throws IOException {
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=total_adoptions_report.xlsx");

		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Total Adoptions");

		Row headerRow = sheet.createRow(0);
		headerRow.createCell(0).setCellValue("Animal Name");
		headerRow.createCell(1).setCellValue("Adopter Name");
		headerRow.createCell(2).setCellValue("Adoption Date");
		headerRow.createCell(3).setCellValue("Status");
		headerRow.createCell(4).setCellValue("Score");

		int rowNum = 1;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		for (AdoptionReportDto adoption : adoptionTrends) {
			Row row = sheet.createRow(rowNum++);
			row.createCell(0).setCellValue(adoption.getAnimalName());
			row.createCell(1).setCellValue(adoption.getAdopterName());
			row.createCell(2).setCellValue(
					adoption.getAdoptionDate() != null ? dateFormat.format(adoption.getAdoptionDate()) : "");
			row.createCell(3).setCellValue(adoption.getStatus().toString());
			row.createCell(4).setCellValue(adoption.getScore());
		}

		Row summaryRow = sheet.createRow(rowNum);
		summaryRow.createCell(0).setCellValue("Total Adoptions");
		summaryRow.createCell(1).setCellValue(totalAdoptions);

		workbook.write(response.getOutputStream());
		workbook.close();
	}

	@Override
	public void generatePdfForTotalDonations(long totalDonationsCount, List<DonationReportDto> donationSummary,
			HttpServletResponse response) throws IOException {
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=total_donations_report.pdf");

		PdfWriter pdfWriter = new PdfWriter(response.getOutputStream());
		PdfDocument pdfDocument = new PdfDocument(pdfWriter);
		Document document = new Document(pdfDocument);

		document.add(new Paragraph("Total Donations Report").setTextAlignment(TextAlignment.CENTER).setFontSize(20)
				.setBold().setFontColor(ColorConstants.GREEN));

		document.add(new Paragraph("Total Donations: " + totalDonationsCount).setFontSize(16));

		Table table = new Table(new float[] { 2, 2, 2, 2, 1, 1 });
		table.setWidth(UnitValue.createPercentValue(100));
		table.addHeaderCell(new Cell().add(new Paragraph("Donor Name").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Amount").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Donation Date").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Donation Type").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Quantity").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Recurring").setBold()));

		for (DonationReportDto donation : donationSummary) {
			table.addCell(new Cell().add(new Paragraph(donation.getDonorName())));

			String amount = donation.getAmount() != null ? donation.getAmount().toString() : "0.0";
			table.addCell(new Cell().add(new Paragraph(amount)));
			String donationDate = donation.getDonationDate() != null ? donation.getDonationDate().toString() : "";
			table.addCell(new Cell().add(new Paragraph(donationDate)));
			table.addCell(new Cell().add(new Paragraph(donation.getDonationType())));
			table.addCell(new Cell()
					.add(new Paragraph(donation.getQuantity() != null ? donation.getQuantity().toString() : "0")));
			table.addCell(new Cell().add(new Paragraph(donation.isRecurring() ? "Yes" : "No")));
		}

		document.add(table);
		document.close();
	}

	@Override
	public void generateExcelForTotalDonations(long totalDonationsCount, List<DonationReportDto> donationSummary,
			HttpServletResponse response) throws IOException {
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=total_donations_report.xlsx");

		XSSFWorkbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Total Donations");

		Row headerRow = sheet.createRow(0);
		headerRow.createCell(0).setCellValue("Donor Name");
		headerRow.createCell(1).setCellValue("Amount");
		headerRow.createCell(2).setCellValue("Donation Date");
		headerRow.createCell(3).setCellValue("Donation Type");
		headerRow.createCell(4).setCellValue("Quantity");
		headerRow.createCell(5).setCellValue("Recurring");

		int rowNum = 1;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Set date format
		for (DonationReportDto donation : donationSummary) {
			Row row = sheet.createRow(rowNum++);
			row.createCell(0).setCellValue(donation.getDonorName());

			BigDecimal amount = donation.getAmount();
			row.createCell(1).setCellValue(amount != null ? amount.doubleValue() : 0.0); // Handle potential null
			Date donationDate = donation.getDonationDate();
			row.createCell(2).setCellValue(donationDate != null ? dateFormat.format(donationDate) : ""); // Format Date
			row.createCell(3).setCellValue(donation.getDonationType());
			Integer quantity = donation.getQuantity();
			row.createCell(4).setCellValue(quantity != null ? quantity : 0);
			row.createCell(5).setCellValue(donation.isRecurring() ? "Yes" : "No"); // Convert boolean to "Yes"/"No"
		}

		Row summaryRow = sheet.createRow(rowNum);
		summaryRow.createCell(0).setCellValue("Total Donations");
		summaryRow.createCell(1).setCellValue((double) totalDonationsCount); // Convert long to double

		workbook.write(response.getOutputStream());
		workbook.close();
	}

	@Override
	public void generatePdfForAnimalHealthStatus(List<AnimalReportDto> filteredAnimals, HttpServletResponse response)
	        throws IOException {
	    response.setContentType("application/pdf");
	    response.setHeader("Content-Disposition", "attachment; filename=animal_health_status_report.pdf");

	    try (PdfWriter pdfWriter = new PdfWriter(response.getOutputStream());
	            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
	            Document document = new Document(pdfDocument)) {

	        document.add(new Paragraph("Animal Health Status Report").setTextAlignment(TextAlignment.CENTER)
	                .setFontSize(20).setBold().setFontColor(ColorConstants.RED));

	        Table table = new Table(new float[] { 2, 2, 2, 2, 2 });
	        table.setWidth(UnitValue.createPercentValue(100));
	        table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Animal Name").setBold()));
	        table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Type").setBold()));
	        table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Health Status").setBold()));
	        table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Doctor Appointment").setBold()));
	        table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Shelter Name").setBold()));

	        for (AnimalReportDto animal : filteredAnimals) {
	            table.addCell(new com.itextpdf.layout.element.Cell()
	                    .add(new Paragraph(animal.getName() != null ? animal.getName() : "Unknown")));
	            table.addCell(new com.itextpdf.layout.element.Cell()
	                    .add(new Paragraph(animal.getType() != null ? animal.getType() : "Unknown")));
	            table.addCell(new com.itextpdf.layout.element.Cell()
	                    .add(new Paragraph(animal.getHealthStatus() != null ? animal.getHealthStatus() : "Unknown")));
	            table.addCell(new com.itextpdf.layout.element.Cell()
	                    .add(new Paragraph(animal.getDoctorAppointment() != null ? animal.getDoctorAppointment() : "No")));
	            table.addCell(new com.itextpdf.layout.element.Cell()
	                    .add(new Paragraph(animal.getShelterName() != null ? animal.getShelterName() : "No Shelter Assigned")));
	        }

	        document.add(table);
	    } catch (IOException e) {
	        throw new IOException("Error generating PDF report: " + e.getMessage());
	    }
	}

	@Override
	public void generateExcelForAnimalHealthStatus(List<AnimalReportDto> filteredAnimals, HttpServletResponse response)
	        throws IOException {
	    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	    response.setHeader("Content-Disposition", "attachment; filename=animal_health_status_report.xlsx");

	    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
	        Sheet sheet = workbook.createSheet("Animal Health Status");

	        Row headerRow = sheet.createRow(0);
	        headerRow.createCell(0).setCellValue("Animal Name");
	        headerRow.createCell(1).setCellValue("Type");
	        headerRow.createCell(2).setCellValue("Health Status");
	        headerRow.createCell(3).setCellValue("Doctor Appointment");
	        headerRow.createCell(4).setCellValue("Shelter Name");

	        int rowNum = 1;
	        for (AnimalReportDto animal : filteredAnimals) {
	            Row row = sheet.createRow(rowNum++);
	            row.createCell(0).setCellValue(animal.getName() != null ? animal.getName() : "Unknown");
	            row.createCell(1).setCellValue(animal.getType() != null ? animal.getType() : "Unknown");
	            row.createCell(2).setCellValue(animal.getHealthStatus() != null ? animal.getHealthStatus() : "Unknown");
	            row.createCell(3)
	                    .setCellValue(animal.getDoctorAppointment() != null ? animal.getDoctorAppointment() : "No");
	            row.createCell(4).setCellValue(
	                    animal.getShelterName() != null ? animal.getShelterName() : "No Shelter Assigned");
	        }

	        for (int i = 0; i < 5; i++) {
	            sheet.autoSizeColumn(i);
	        }

	        workbook.write(response.getOutputStream());
	    } catch (IOException e) {
	        throw new IOException("Error generating Excel report: " + e.getMessage());
	    }
	}


	@Override
	public void generatePdfForAdoptionTrends(long totalAdoptions, List<AdoptionReportDto> adoptionTrends,
			HttpServletResponse response) throws IOException {
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=adoption_trends_report.pdf");

		PdfWriter pdfWriter = new PdfWriter(response.getOutputStream());
		PdfDocument pdfDocument = new PdfDocument(pdfWriter);
		Document document = new Document(pdfDocument);

		document.add(new Paragraph("Adoption Trends Report").setTextAlignment(TextAlignment.CENTER).setFontSize(20)
				.setBold().setFontColor(ColorConstants.BLUE));

		document.add(new Paragraph("Total Adoptions: " + totalAdoptions).setFontSize(16));

		// Create a table for adoption trends
		Table table = new Table(new float[] { 2, 2, 2, 2, 2, 1 }); // Adjust column widths as needed
		table.setWidth(UnitValue.createPercentValue(100));
		table.addHeaderCell(new Cell().add(new Paragraph("Animal Name").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Adopter Name").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Adoption Date").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Status").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Request Date").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Score").setBold()));

		for (AdoptionReportDto trend : adoptionTrends) {
			table.addCell(new Cell().add(new Paragraph(trend.getAnimalName())));
			table.addCell(new Cell().add(new Paragraph(trend.getAdopterName()))); // Added missing parenthesis here

			// Check if adoptionDate is null before converting to string
			String adoptionDate = (trend.getAdoptionDate() != null) ? trend.getAdoptionDate().toString() : "N/A";
			table.addCell(new Cell().add(new Paragraph(adoptionDate)));

			table.addCell(new Cell().add(new Paragraph(trend.getStatus().toString())));

			// Check if requestDate is null before converting to string
			String requestDate = (trend.getRequestDate() != null) ? trend.getRequestDate().toString() : "N/A";
			table.addCell(new Cell().add(new Paragraph(requestDate)));

			table.addCell(new Cell().add(new Paragraph(String.valueOf(trend.getScore()))));
		}

		document.add(table);
		document.close();
	}

	@Override
	public void generateExcelForAdoptionTrends(long totalAdoptions, List<AdoptionReportDto> adoptionTrends,
			HttpServletResponse response) throws IOException {
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=adoption_trends_report.xlsx");

		XSSFWorkbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Adoption Trends");

		Row headerRow = sheet.createRow(0);
		headerRow.createCell(0).setCellValue("Animal Name");
		headerRow.createCell(1).setCellValue("Adopter Name");
		headerRow.createCell(2).setCellValue("Adoption Date");
		headerRow.createCell(3).setCellValue("Status");
		headerRow.createCell(4).setCellValue("Request Date");
		headerRow.createCell(5).setCellValue("Score");

		int rowNum = 1;
		for (AdoptionReportDto trend : adoptionTrends) {
			Row row = sheet.createRow(rowNum++);
			row.createCell(0).setCellValue(trend.getAnimalName() != null ? trend.getAnimalName() : "N/A");
			row.createCell(1).setCellValue(trend.getAdopterName() != null ? trend.getAdopterName() : "N/A");

			// Check for null adoptionDate and convert it to string if not null
			String adoptionDate = (trend.getAdoptionDate() != null) ? trend.getAdoptionDate().toString() : "N/A";
			row.createCell(2).setCellValue(adoptionDate);

			// Check for null status and convert it to string if not null
			String status = (trend.getStatus() != null) ? trend.getStatus().toString() : "N/A";
			row.createCell(3).setCellValue(status);

			// Check for null requestDate and convert it to string if not null
			String requestDate = (trend.getRequestDate() != null) ? trend.getRequestDate().toString() : "N/A";
			row.createCell(4).setCellValue(requestDate);

			row.createCell(5).setCellValue(trend.getScore());
		}

		Row summaryRow = sheet.createRow(rowNum);
		summaryRow.createCell(0).setCellValue("Total Adoptions");
		summaryRow.createCell(1).setCellValue(totalAdoptions);

		workbook.write(response.getOutputStream());
		workbook.close();
	}

	@Override
	public void generatePdfForAnimalDistribution(List<AnimalCountDto> animalDistribution, HttpServletResponse response)
			throws IOException {
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=animal_distribution_report.pdf");

		PdfWriter pdfWriter = new PdfWriter(response.getOutputStream());
		PdfDocument pdfDocument = new PdfDocument(pdfWriter);
		Document document = new Document(pdfDocument);

		document.add(new Paragraph("Animal Distribution Report").setTextAlignment(TextAlignment.CENTER).setFontSize(20)
				.setBold().setFontColor(ColorConstants.BLUE));

		Table table = new Table(new float[] { 1, 1 }); // One column for animal type, one for count
		table.setWidth(UnitValue.createPercentValue(100));
		table.addHeaderCell(new Cell().add(new Paragraph("Animal Type").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Count").setBold()));

		for (AnimalCountDto distribution : animalDistribution) {
			table.addCell(new Cell().add(new Paragraph(distribution.getType())));
			table.addCell(new Cell().add(new Paragraph(String.valueOf(distribution.getCount()))));
		}

		document.add(table);
		document.close();
	}

	@Override
	public void generateExcelForAnimalDistribution(List<AnimalCountDto> animalDistribution,
			HttpServletResponse response) throws IOException {
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=animal_distribution_report.xlsx");

		XSSFWorkbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Animal Distribution");

		Row headerRow = sheet.createRow(0);
		headerRow.createCell(0).setCellValue("Animal Type");
		headerRow.createCell(1).setCellValue("Count");

		int rowNum = 1;
		for (AnimalCountDto distribution : animalDistribution) {
			Row row = sheet.createRow(rowNum++);
			row.createCell(0).setCellValue(distribution.getType());
			row.createCell(1).setCellValue(distribution.getCount());
		}

		workbook.write(response.getOutputStream());
		workbook.close();
	}

	@Override
	public void generatePdfForShelterCapacity(List<ShelterCapacityDto> shelterCapacity, HttpServletResponse response)
			throws IOException {
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=shelter_capacity_report.pdf");

		PdfWriter pdfWriter = new PdfWriter(response.getOutputStream());
		PdfDocument pdfDocument = new PdfDocument(pdfWriter);
		Document document = new Document(pdfDocument);

		document.add(new Paragraph("Shelter Capacity Report").setTextAlignment(TextAlignment.CENTER).setFontSize(20)
				.setBold().setFontColor(ColorConstants.BLUE));

		for (ShelterCapacityDto capacity : shelterCapacity) {
			document.add(new Paragraph("Shelter: " + capacity.getShelterName()).setBold().setFontSize(16));
			document.add(new Paragraph("Location: " + capacity.getLocation()));
			document.add(new Paragraph("Contact Details: " + capacity.getContactDetails()));
			document.add(new Paragraph("Current Occupancy: " + capacity.getCurrentOccupancy()));
			document.add(new Paragraph("Max Capacity: " + capacity.getCapacity()));
			document.add(new Paragraph("\n")); // Add space between shelters
		}

		document.close();
	}

	@Override
	public void generateExcelForShelterCapacity(List<ShelterCapacityDto> shelterCapacity, HttpServletResponse response)
			throws IOException {
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=shelter_capacity_report.xlsx");

		XSSFWorkbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Shelter Capacity");

		Row headerRow = sheet.createRow(0);
		headerRow.createCell(0).setCellValue("Shelter Name");
		headerRow.createCell(1).setCellValue("Location");
		headerRow.createCell(2).setCellValue("Contact Details");
		headerRow.createCell(3).setCellValue("Current Occupancy");
		headerRow.createCell(4).setCellValue("Max Capacity");

		int rowNum = 1;
		for (ShelterCapacityDto capacity : shelterCapacity) {
			Row row = sheet.createRow(rowNum++);
			row.createCell(0).setCellValue(capacity.getShelterName());
			row.createCell(1).setCellValue(capacity.getLocation());
			row.createCell(2).setCellValue(capacity.getContactDetails());
			row.createCell(3).setCellValue(capacity.getCurrentOccupancy());
			row.createCell(4).setCellValue(capacity.getCapacity());
		}

		workbook.write(response.getOutputStream());
		workbook.close();
	}

	@Override
	// Generates PDF for incident report summary
	public void generatePdfForReportSummary(List<ReportSummaryDto> incidentReports, HttpServletResponse response)
	        throws IOException {
	    response.setContentType("application/pdf");
	    response.setHeader("Content-Disposition", "attachment; filename=report_summary.pdf");

	    try (PdfWriter pdfWriter = new PdfWriter(response.getOutputStream());
	         PdfDocument pdfDocument = new PdfDocument(pdfWriter);
	         Document document = new Document(pdfDocument)) {

	        // Title
	        document.add(new Paragraph("Report Summary")
	                .setTextAlignment(TextAlignment.CENTER)
	                .setFontSize(20)
	                .setBold()
	                .setFontColor(ColorConstants.BLUE));

	        document.add(new Paragraph("\nIncident Reports:").setBold().setFontSize(18));

	        // Table
	        Table table = new Table(new float[]{1, 1, 2, 2, 2, 2, 2});
	        table.setWidth(UnitValue.createPercentValue(100));
	        table.setHorizontalAlignment(HorizontalAlignment.CENTER);

	        // Header cells
	        for (String header : new String[]{"Report Type", "Status", "Description", "Report Date", "Location",
	                "Address", "Rejection Reason"}) {
	            table.addHeaderCell(new Cell().add(new Paragraph(header).setBold()));
	        }

	        // Date format
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	        for (ReportSummaryDto incident : incidentReports) {
	            table.addCell(new Cell().add(new Paragraph(incident.getReportType())));
	            table.addCell(new Cell().add(new Paragraph(incident.getStatus())));
	            table.addCell(new Cell().add(new Paragraph(incident.getDescription())));
	            table.addCell(new Cell().add(new Paragraph(incident.getReportDate().format(formatter))));
	            table.addCell(new Cell().add(new Paragraph(incident.getLocation())));
	            table.addCell(new Cell().add(new Paragraph(incident.getAddress())));
	            table.addCell(new Cell().add(new Paragraph(incident.getRejectionReason() != null ? incident.getRejectionReason() : "N/A")));
	        }

	        document.add(table);
	        // Optional: Add footer with page number or generated date
	        document.add(new Paragraph("\nGenerated on: " + LocalDate.now().format(formatter)));
	    } catch (Exception e) {
	        // Handle exceptions (log, rethrow, etc.)
	        e.printStackTrace(); // Consider logging this properly
	        throw new IOException("Error generating PDF", e);
	    }
	}

	@Override
	// Generates Excel for incident report summary
	public void generateExcelForReportSummary(List<ReportSummaryDto> incidentReports, HttpServletResponse response)
	        throws IOException {
	    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	    response.setHeader("Content-Disposition", "attachment; filename=report_summary.xlsx");

	    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
	        Sheet sheet = workbook.createSheet("Report Summary");

	        // Header row
	        Row headerRow = sheet.createRow(0);
	        String[] headers = {"Report Type", "Status", "Description", "Report Date", "Location", "Address", "Rejection Reason"};
	        for (int i = 0; i < headers.length; i++) {
	            headerRow.createCell(i).setCellValue(headers[i]);
	        }

	        // Incident rows
	        int rowNum = 1; // Starting row for incidents
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	        for (ReportSummaryDto incident : incidentReports) {
	            Row incidentRow = sheet.createRow(rowNum++);
	            incidentRow.createCell(0).setCellValue(incident.getReportType());
	            incidentRow.createCell(1).setCellValue(incident.getStatus());
	            incidentRow.createCell(2).setCellValue(incident.getDescription());
	            incidentRow.createCell(3).setCellValue(incident.getReportDate().format(formatter));
	            incidentRow.createCell(4).setCellValue(incident.getLocation());
	            incidentRow.createCell(5).setCellValue(incident.getAddress());
	            incidentRow.createCell(6).setCellValue(incident.getRejectionReason() != null ? incident.getRejectionReason() : "N/A");
	        }

	        // Auto size columns
	        for (int i = 0; i < headers.length; i++) {
	            sheet.autoSizeColumn(i);
	        }

	        workbook.write(response.getOutputStream());
	    } catch (Exception e) {
	        // Handle exceptions (log, rethrow, etc.)
	        e.printStackTrace(); // Consider logging this properly
	        throw new IOException("Error generating Excel", e);
	    }
	}

	
	@Override
	public void generateExcelForDonationSummary(List<DonationReportDto> donationSummary, long totalDonationsCount,
			HttpServletResponse response) throws IOException {
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=donation_summary.xlsx");

		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Donation Summary");

		Row headerRow = sheet.createRow(0);
		headerRow.createCell(0).setCellValue("Donor Name");
		headerRow.createCell(1).setCellValue("Amount");
		headerRow.createCell(2).setCellValue("Date");
		headerRow.createCell(3).setCellValue("Donation Type");
		headerRow.createCell(4).setCellValue("Quantity");
		headerRow.createCell(5).setCellValue("Recurring");

		int rowNum = 1;
		for (DonationReportDto donation : donationSummary) {
			Row row = sheet.createRow(rowNum++);
			row.createCell(0).setCellValue(donation.getDonorName() != null ? donation.getDonorName() : "N/A");

			// Check for null and convert amount to double if not null
			if (donation.getAmount() != null) {
				row.createCell(1).setCellValue(donation.getAmount().doubleValue());
			} else {
				row.createCell(1).setCellValue(0); // or handle it as you see fit
			}

			// Check for null before converting LocalDate to String
			row.createCell(2)
					.setCellValue(donation.getDonationDate() != null ? donation.getDonationDate().toString() : "N/A");
			row.createCell(3).setCellValue(donation.getDonationType() != null ? donation.getDonationType() : "N/A");
			row.createCell(4).setCellValue(donation.getQuantity() != null ? donation.getQuantity() : 0);
			row.createCell(5).setCellValue(donation.isRecurring() ? "Yes" : "No");
		}

		Row summaryRow = sheet.createRow(rowNum);
		summaryRow.createCell(0).setCellValue("Total Donations");
		summaryRow.createCell(1).setCellValue(totalDonationsCount);

		workbook.write(response.getOutputStream());
		workbook.close();
	}

	@Override
	public void generatePdfForDonationSummary(List<DonationReportDto> donationSummary, long totalDonationsCount,
			HttpServletResponse response) throws IOException {
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=donation_summary.pdf");

		PdfWriter pdfWriter = new PdfWriter(response.getOutputStream());
		PdfDocument pdfDocument = new PdfDocument(pdfWriter);
		Document document = new Document(pdfDocument);

		document.add(new Paragraph("Donation Summary").setTextAlignment(TextAlignment.CENTER).setFontSize(20).setBold()
				.setFontColor(ColorConstants.BLUE));

		Table table = new Table(new float[] { 1, 1, 1, 1, 1, 1 });
		table.setWidth(UnitValue.createPercentValue(100));
		table.addHeaderCell(new Cell().add(new Paragraph("Donor Name").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Amount").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Date").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Donation Type").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Quantity").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Recurring").setBold()));

		for (DonationReportDto donation : donationSummary) {
			table.addCell(
					new Cell().add(new Paragraph(donation.getDonorName() != null ? donation.getDonorName() : "N/A")));

			// Handle null amounts
			if (donation.getAmount() != null) {
				table.addCell(new Cell().add(new Paragraph(donation.getAmount().toString())));
			} else {
				table.addCell(new Cell().add(new Paragraph("0"))); // or handle it as you see fit
			}

			// Handle null donation date
			table.addCell(new Cell().add(
					new Paragraph(donation.getDonationDate() != null ? donation.getDonationDate().toString() : "N/A")));
			table.addCell(new Cell()
					.add(new Paragraph(donation.getDonationType() != null ? donation.getDonationType() : "N/A")));
			table.addCell(new Cell()
					.add(new Paragraph(String.valueOf(donation.getQuantity() != null ? donation.getQuantity() : 0))));
			table.addCell(new Cell().add(new Paragraph(donation.isRecurring() ? "Yes" : "No")));
		}

		// Adding a summary row for total donations
		table.addCell(new Cell(1, 1).add(new Paragraph("Total Donations")).setBold());
		table.addCell(new Cell(1, 5).add(new Paragraph(String.valueOf(totalDonationsCount))).setBold());

		document.add(table);
		document.close();
	}

}