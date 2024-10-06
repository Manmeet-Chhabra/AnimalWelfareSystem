package com.manmeet.animalsys.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "animals")
public class Animal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String type; // e.g., Dog, Cat, etc.

	@Column(name = "health_status")
	private String healthStatus;

	@Column(name = "doctor_appointment")
	private String doctorAppointment; // or any other relevant fields

	@Column(name = "picture_url")
	private String pictureUrl; // Field to store the animal picture URL
	
	 @ManyToOne
	    @JoinColumn(name = "shelter_id") // This will create a shelter_id column in the animals table
	    private Shelter shelter; // Reference to the Shelter entity

	public Animal() {
	}

	public Animal(Long id, String name, String type, String healthStatus, String doctorAppointment, String pictureUrl,
			Shelter shelter) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.healthStatus = healthStatus;
		this.doctorAppointment = doctorAppointment;
		this.pictureUrl = pictureUrl;
		this.shelter = shelter;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getDoctorAppointment() {
		return doctorAppointment;
	}

	public void setDoctorAppointment(String doctorAppointment) {
		this.doctorAppointment = doctorAppointment;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}

	public Shelter getShelter() {
		return shelter;
	}

	public void setShelter(Shelter shelter) {
		this.shelter = shelter;
	}

	
}