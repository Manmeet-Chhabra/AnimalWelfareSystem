package com.manmeet.animalsys.dto;

public class ShelterCapacityDto {
    private String shelterName;
    private String location;
    private String contactDetails;
    private Integer currentOccupancy; // Ensure this matches
    private Integer capacity; // Ensure this matches

    // Constructor
    public ShelterCapacityDto(String shelterName, String location, String contactDetails, Integer currentOccupancy, Integer capacity) {
        this.shelterName = shelterName;
        this.location = location;
        this.contactDetails = contactDetails;
        this.currentOccupancy = currentOccupancy;
        this.capacity = capacity;
    }

    // Getters
    public String getShelterName() {
        return shelterName;
    }

    public String getLocation() {
        return location;
    }

    public String getContactDetails() {
        return contactDetails;
    }

    public Integer getCurrentOccupancy() { // Ensure this method is present
        return currentOccupancy;
    }

    public Integer getCapacity() {
        return capacity;
    }

	public void setShelterName(String shelterName) {
		this.shelterName = shelterName;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setContactDetails(String contactDetails) {
		this.contactDetails = contactDetails;
	}

	public void setCurrentOccupancy(Integer currentOccupancy) {
		this.currentOccupancy = currentOccupancy;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}
}
