package com.manmeet.animalsys.dto;

import java.time.LocalDate;

import com.manmeet.animalsys.entity.AdoptionRequestStatus;

public class AdoptionReportDto {

	private String animalName;
    private String adopterName;
    private LocalDate adoptionDate; // Assuming this is the date you want to record
    private AdoptionRequestStatus status; // Added status
    private LocalDate requestDate; // Added request date
    private int score; // Added score

    // Constructor with all parameters
    public AdoptionReportDto(String animalName, String adopterName, LocalDate adoptionDate, 
                              AdoptionRequestStatus status, LocalDate requestDate, 
                               int score) {
        this.animalName = animalName;
        this.adopterName = adopterName;
        this.adoptionDate = adoptionDate;
        this.status = status;
        this.requestDate = requestDate;
        this.score = score;
    }


    // Getters and setters for new fields
    

    
    public AdoptionRequestStatus getStatus() {
		return status;
	}


	public void setStatus(AdoptionRequestStatus status) {
		this.status = status;
	}


	

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

	public String getAnimalName() {
		return animalName;
	}

	public void setAnimalName(String animalName) {
		this.animalName = animalName;
	}

	public String getAdopterName() {
		return adopterName;
	}

	public void setAdopterName(String adopterName) {
		this.adopterName = adopterName;
	}


	public LocalDate getAdoptionDate() {
		return adoptionDate;
	}


	public void setAdoptionDate(LocalDate adoptionDate) {
		this.adoptionDate = adoptionDate;
	}


	public LocalDate getRequestDate() {
		return requestDate;
	}


	public void setRequestDate(LocalDate requestDate) {
		this.requestDate = requestDate;
	}

	

	// Getters and Setters
}
