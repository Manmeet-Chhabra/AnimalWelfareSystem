package com.manmeet.animalsys.dto;

import java.time.LocalDateTime;

public class ReportSummaryDto {

    private String reportType;
    private String status;
    private String description;
    private LocalDateTime reportDate;
    private String location;
    private String address;
    private String rejectionReason;

    // Constructor with all parameters
    public ReportSummaryDto(String reportType, String status, String description, 
                            LocalDateTime reportDate, String location, 
                            String address, String rejectionReason) {
        this.reportType = reportType;
        this.status = status;
        this.description = description;
        this.reportDate = reportDate;
        this.location = location;
        this.address = address;
        this.rejectionReason = rejectionReason;
    }

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getReportDate() {
		return reportDate;
	}

	public void setReportDate(LocalDateTime reportDate) {
		this.reportDate = reportDate;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	
}
