package com.manmeet.animalsys.utility;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.manmeet.animalsys.dto.AdoptionReportDto;
import com.manmeet.animalsys.dto.AnimalCountDto;
import com.manmeet.animalsys.dto.AnimalReportDto;
import com.manmeet.animalsys.dto.DonationReportDto;
import com.manmeet.animalsys.dto.ReportSummaryDto;
import com.manmeet.animalsys.dto.ShelterCapacityDto;

import jakarta.servlet.http.HttpServletResponse;

public class ReportUtil {

	public static void generatePdfForTotalAdoptions(long totalAdoptions, List<AdoptionReportDto> adoptionTrends,
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

	public static void generateExcelForTotalAdoptions(long totalAdoptions, List<AdoptionReportDto> adoptionTrends,
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

	public static void generatePdfForTotalDonations(long totalDonationsCount, List<DonationReportDto> donationSummary,
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
			table.addCell(new Cell().add(new Paragraph(donation.getQuantity() != null ? donation.getQuantity().toString() : "0")));
			table.addCell(new Cell().add(new Paragraph(donation.isRecurring() ? "Yes" : "No")));
		}

		document.add(table);
		document.close();
	}

	public static void generateExcelForTotalDonations(long totalDonationsCount, List<DonationReportDto> donationSummary,
			HttpServletResponse response) throws IOException {
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=total_donations_report.xlsx");

		Workbook workbook = new XSSFWorkbook();
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

	public static void generatePdfForAnimalHealthStatus(List<AnimalReportDto> filteredAnimals,
			HttpServletResponse response) throws IOException {
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=animal_health_status_report.pdf");

		PdfWriter pdfWriter = new PdfWriter(response.getOutputStream());
		PdfDocument pdfDocument = new PdfDocument(pdfWriter);
		Document document = new Document(pdfDocument);

		document.add(new Paragraph("Animal Health Status Report").setTextAlignment(TextAlignment.CENTER).setFontSize(20)
				.setBold().setFontColor(ColorConstants.RED));

		Table table = new Table(new float[] { 2, 2, 2, 2, 2 });
		table.setWidth(UnitValue.createPercentValue(100));
		table.addHeaderCell(new Cell().add(new Paragraph("Animal Name").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Type").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Health Status").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Doctor Appointment").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Shelter Name").setBold()));

		for (AnimalReportDto animal : filteredAnimals) {
			table.addCell(new Cell().add(new Paragraph(animal.getName())));
			table.addCell(new Cell().add(new Paragraph(animal.getType())));
			table.addCell(new Cell().add(new Paragraph(animal.getHealthStatus())));
			table.addCell(new Cell()
					.add(new Paragraph(animal.getDoctorAppointment() != null ? animal.getDoctorAppointment() : "No")));
			table.addCell(new Cell().add(
					new Paragraph(animal.getShelterName() != null ? animal.getShelterName() : "No Shelter Assigned")));
		}

		document.add(table);
		document.close();
	}

	public static void generateExcelForAnimalHealthStatus(List<AnimalReportDto> filteredAnimals,
			HttpServletResponse response) throws IOException {
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=animal_health_status_report.xlsx");

		Workbook workbook = new XSSFWorkbook();
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
			row.createCell(0).setCellValue(animal.getName());
			row.createCell(1).setCellValue(animal.getType());
			row.createCell(2).setCellValue(animal.getHealthStatus());
			row.createCell(3)
					.setCellValue(animal.getDoctorAppointment() != null ? animal.getDoctorAppointment() : "No");
			row.createCell(4)
					.setCellValue(animal.getShelterName() != null ? animal.getShelterName() : "No Shelter Assigned");
		}

		workbook.write(response.getOutputStream());
		workbook.close();
	}

	public static void generatePdfForAdoptionTrends(long totalAdoptions, List<AdoptionReportDto> adoptionTrends,
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
			table.addCell(new Cell().add(new Paragraph(trend.getAdopterName())));
			table.addCell(new Cell().add(new Paragraph(trend.getAdoptionDate().toString())));
			table.addCell(new Cell().add(new Paragraph(trend.getStatus().toString())));
			table.addCell(new Cell().add(new Paragraph(trend.getRequestDate().toString())));
			table.addCell(new Cell().add(new Paragraph(String.valueOf(trend.getScore()))));
		}

		document.add(table);
		document.close();
	}

	public static void generateExcelForAdoptionTrends(long totalAdoptions, List<AdoptionReportDto> adoptionTrends,
			HttpServletResponse response) throws IOException {
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=adoption_trends_report.xlsx");

		Workbook workbook = new XSSFWorkbook();
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
			row.createCell(0).setCellValue(trend.getAnimalName());
			row.createCell(1).setCellValue(trend.getAdopterName());
			row.createCell(2).setCellValue(trend.getAdoptionDate().toString());
			row.createCell(3).setCellValue(trend.getStatus().toString());
			row.createCell(4).setCellValue(trend.getRequestDate().toString());
			row.createCell(5).setCellValue(trend.getScore());
		}

		Row summaryRow = sheet.createRow(rowNum);
		summaryRow.createCell(0).setCellValue("Total Adoptions");
		summaryRow.createCell(1).setCellValue(totalAdoptions);

		workbook.write(response.getOutputStream());
		workbook.close();
	}

	public static void generatePdfForAnimalDistribution(List<AnimalCountDto> animalDistribution,
			HttpServletResponse response) throws IOException {
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=animal_distribution_report.pdf");

		PdfWriter pdfWriter = new PdfWriter(response.getOutputStream());
		PdfDocument pdfDocument = new PdfDocument(pdfWriter);
		Document document = new Document(pdfDocument);

		document.add(new Paragraph("Animal Distribution Report").setTextAlignment(TextAlignment.CENTER).setFontSize(20)
				.setBold().setFontColor(ColorConstants.BLUE));

		Table table = new Table(new float[] { 1, 1 });
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

	public static void generateExcelForAnimalDistribution(List<AnimalCountDto> animalDistribution,
			HttpServletResponse response) throws IOException {
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=animal_distribution_report.xlsx");

		Workbook workbook = new XSSFWorkbook();
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

	public static void generatePdfForShelterCapacity(List<ShelterCapacityDto> shelterCapacity,
			HttpServletResponse response) throws IOException {
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

	public static void generateExcelForShelterCapacity(List<ShelterCapacityDto> shelterCapacity,
			HttpServletResponse response) throws IOException {
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=shelter_capacity_report.xlsx");

		Workbook workbook = new XSSFWorkbook();
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

	public static void generatePdfForReportSummary(List<ReportSummaryDto> incidentReports,
			ReportSummaryDto reportSummary, HttpServletResponse response) throws IOException {
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=report_summary.pdf");

		PdfWriter pdfWriter = new PdfWriter(response.getOutputStream());
		PdfDocument pdfDocument = new PdfDocument(pdfWriter);
		Document document = new Document(pdfDocument);

		document.add(new Paragraph("Report Summary").setTextAlignment(TextAlignment.CENTER).setFontSize(20).setBold()
				.setFontColor(ColorConstants.BLUE));

		document.add(new Paragraph("\nIncident Reports:").setBold().setFontSize(18));

		Table table = new Table(new float[] { 1, 1, 2, 2, 2, 2, 2 });
		table.setWidth(UnitValue.createPercentValue(100));
		table.addHeaderCell(new Cell().add(new Paragraph("Report Type").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Status").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Description").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Report Date").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Location").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Address").setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Rejection Reason").setBold()));

		for (ReportSummaryDto incident : incidentReports) {
			table.addCell(new Cell().add(new Paragraph(incident.getReportType())));
			table.addCell(new Cell().add(new Paragraph(incident.getStatus())));
			table.addCell(new Cell().add(new Paragraph(incident.getDescription())));
			table.addCell(new Cell().add(new Paragraph(incident.getReportDate().toString()))); // Format as needed
			table.addCell(new Cell().add(new Paragraph(incident.getLocation())));
			table.addCell(new Cell().add(new Paragraph(incident.getAddress())));
			table.addCell(new Cell()
					.add(new Paragraph(incident.getRejectionReason() != null ? incident.getRejectionReason() : "N/A")));
		}

		document.add(table);
		document.close();
	}

	public static void generateExcelForReportSummary(List<ReportSummaryDto> incidentReports,
			ReportSummaryDto reportSummary, HttpServletResponse response) throws IOException {
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=report_summary.xlsx");

		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Report Summary");

		Row row = sheet.createRow(0);
		row = sheet.createRow(4);
		row.createCell(0).setCellValue("Report Type");
		row.createCell(1).setCellValue("Status");
		row.createCell(2).setCellValue("Description");
		row.createCell(3).setCellValue("Report Date");
		row.createCell(4).setCellValue("Location");
		row.createCell(5).setCellValue("Address");
		row.createCell(6).setCellValue("Rejection Reason");

		int rowNum = 5; // Starting row for incidents
		for (ReportSummaryDto incident : incidentReports) {
			Row incidentRow = sheet.createRow(rowNum++);
			incidentRow.createCell(0).setCellValue(incident.getReportType());
			incidentRow.createCell(1).setCellValue(incident.getStatus());
			incidentRow.createCell(2).setCellValue(incident.getDescription());
			incidentRow.createCell(3).setCellValue(incident.getReportDate().toString()); // Format as needed
			incidentRow.createCell(4).setCellValue(incident.getLocation());
			incidentRow.createCell(5).setCellValue(incident.getAddress());
			incidentRow.createCell(6)
					.setCellValue(incident.getRejectionReason() != null ? incident.getRejectionReason() : "N/A");
		}

		workbook.write(response.getOutputStream());
		workbook.close();
	}

	public static void generatePdfForAnimalReports(List<AnimalReportDto> animalReports, HttpServletResponse response)
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
					.add(new Paragraph(report.getDoctorAppointment() == null ? "N/A" : report.getDoctorAppointment())));
			table.addCell(new Cell().add(
					new Paragraph(report.getShelterName() != null ? report.getShelterName() : "No Shelter Assigned")));
			table.addCell(new Cell().add(
					new Paragraph(report.getAdoptionStatus() != null ? report.getAdoptionStatus().toString() : "N/A")));
		}

		document.add(table);
		document.close();
	}

	public static void generateExcelForAnimalReports(List<AnimalReportDto> animalReports, HttpServletResponse response)
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

	public static void generateExcelForDonationSummary(List<DonationReportDto> donationSummary,
			long totalDonationsCount, HttpServletResponse response) throws IOException {
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
			row.createCell(0).setCellValue(donation.getDonorName());
			row.createCell(1).setCellValue(donation.getAmount().doubleValue()); // Assuming amount is BigDecimal
			row.createCell(2).setCellValue(donation.getDonationDate().toString()); // Assuming donationDate is LocalDate
			row.createCell(3).setCellValue(donation.getDonationType());
			row.createCell(4).setCellValue(donation.getQuantity());
			row.createCell(5).setCellValue(donation.isRecurring() ? "Yes" : "No");
		}

		Row summaryRow = sheet.createRow(rowNum);
		summaryRow.createCell(0).setCellValue("Total Donations");
		summaryRow.createCell(1).setCellValue(totalDonationsCount);

		workbook.write(response.getOutputStream());
		workbook.close();
	}

	public static void generatePdfForDonationSummary(List<DonationReportDto> donationSummary, long totalDonationsCount,
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
			table.addCell(new Cell().add(new Paragraph(donation.getDonorName())));
			table.addCell(new Cell().add(new Paragraph(donation.getAmount().toString())));
			table.addCell(new Cell().add(new Paragraph(donation.getDonationDate().toString())));
			table.addCell(new Cell().add(new Paragraph(donation.getDonationType())));
			table.addCell(new Cell().add(new Paragraph(String.valueOf(donation.getQuantity()))));
			table.addCell(new Cell().add(new Paragraph(donation.isRecurring() ? "Yes" : "No")));
		}

		table.addCell(new Cell(1, 1).add(new Paragraph("Total Donations")).setBold());
		table.addCell(new Cell(1, 5).add(new Paragraph(String.valueOf(totalDonationsCount))).setBold());

		document.add(table);
		document.close();
	}

}
