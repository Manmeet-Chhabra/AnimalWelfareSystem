package com.manmeet.animalsys.dto;

import com.manmeet.animalsys.entity.AdoptionStatus;

public class AnimalReportDto {
	private String name;
    private String type; // e.g., Dog, Cat
    private String healthStatus;
    private String pictureUrl; // Optional: for displaying the animal's picture
    private String shelterName; // For associating with the shelter
    private String shelterLocation; // Add this field
    private String doctorAppointment; // Add this field
    private AdoptionStatus adoptionStatus;
	
    public AnimalReportDto(String name, String type, String healthStatus, String shelterName, String doctorAppointment) {
        this.name = name;
        this.type = type;
        this.healthStatus = healthStatus;
        this.shelterName = shelterName;
        this.doctorAppointment = doctorAppointment;
    }
    
	public String getShelterLocation() {
		return shelterLocation;
	}

	public void setShelterLocation(String shelterLocation) {
		this.shelterLocation = shelterLocation;
	}

	public String getDoctorAppointment() {
		return doctorAppointment;
	}

	public void setDoctorAppointment(String doctorAppointment) {
		this.doctorAppointment = doctorAppointment;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHealthStatus() {
		return healthStatus;
	}

	public void setHealthStatus(String healthStatus) {
		this.healthStatus = healthStatus;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}

	public String getShelterName() {
		return shelterName;
	}

	public void setShelterName(String shelterName) {
		this.shelterName = shelterName;
	}

	public AdoptionStatus getAdoptionStatus() {
		return adoptionStatus;
	}

	public void setAdoptionStatus(AdoptionStatus adoptionStatus) {
		this.adoptionStatus = adoptionStatus;
	}

	public AnimalReportDto() {
	}

	public AnimalReportDto(String name, String type, String healthStatus, String pictureUrl, String shelterName,
			AdoptionStatus adoptionStatus) {
		this.name = name;
		this.type = type;
		this.healthStatus = healthStatus;
		this.pictureUrl = pictureUrl;
		this.shelterName = shelterName;
		this.adoptionStatus = adoptionStatus;
	}

	// Constructors, Getters, and Setters
}
