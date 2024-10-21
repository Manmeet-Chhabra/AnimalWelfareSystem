package com.manmeet.animalsys.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Report {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private ReportType type; // ABUSE or ACCIDENT

	@NotBlank
	private String description;

	@ManyToOne
	@JoinColumn(name = "animal_id", nullable = true)
	private Animal animal; // optional, might not always involve an animal

	private String location; // store location as a string (could use more advanced types like spatial data)

	private String address;

	// Add a new field for status
	@Column(nullable = false)
	private String status; // e.g., "pending", "approved", "rejected"

	private LocalDateTime reportDate; // timestamp for when the report was submitted

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User reportedBy; // the user who submitted the report

	@OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
	private List<Attachment> attachments; // file attachments (photos/videos)

	@Column(length = 255) // Adjust length as needed
    private String rejectionReason;

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ReportType getType() {
		return type;
	}

	public void setType(ReportType type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Animal getAnimal() {
		return animal;
	}

	public void setAnimal(Animal animal) {
		this.animal = animal;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public LocalDateTime getReportDate() {
		return reportDate;
	}

	public void setReportDate(LocalDateTime reportDate) {
		this.reportDate = reportDate;
	}

	public User getReportedBy() {
		return reportedBy;
	}

	public void setReportedBy(User reportedBy) {
		this.reportedBy = reportedBy;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	@PrePersist
	public void prePersist() {
		this.reportDate = LocalDateTime.now();
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	// Getters and Setters...
}
