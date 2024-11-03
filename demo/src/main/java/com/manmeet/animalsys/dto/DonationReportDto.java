package com.manmeet.animalsys.dto;

import java.math.BigDecimal;
import java.util.Date;

public class DonationReportDto {

	    private String donorName;
	    private BigDecimal amount;
	    private Date donationDate;
	    private String donationType; // Type of donation
	    private Integer quantity; // Quantity of items donated, if applicable
	    private boolean recurring; // Indicates if the donation is recurring

	    // Constructor
	    public DonationReportDto(String donorName, BigDecimal amount, Date donationDate, String donationType, Integer quantity, boolean recurring) {
	        this.donorName = donorName;
	        this.amount = amount;
	        this.donationDate = donationDate;
	        this.donationType = donationType; // Ensure this is included
	        this.quantity = quantity;           // Ensure this is included
	        this.recurring = recurring;         // Ensure this is included
	    }
 // Constructor for essential details only
    public DonationReportDto(String donorName, BigDecimal amount, Date donationDate) {
        this(donorName, amount, donationDate, null, null, false); // Set default values for optional fields
    }
	
	public String getDonationType() {
		return donationType;
	}
	public void setDonationType(String donationType) {
		this.donationType = donationType;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public boolean isRecurring() {
		return recurring;
	}
	public void setRecurring(boolean recurring) {
		this.recurring = recurring;
	}
	public String getDonorName() {
		return donorName;
	}

	public void setDonorName(String donorName) {
		this.donorName = donorName;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Date getDonationDate() {
		return donationDate;
	}

	public void setDonationDate(Date donationDate) {
		this.donationDate = donationDate;
	}

	// Getters and Setters
}
