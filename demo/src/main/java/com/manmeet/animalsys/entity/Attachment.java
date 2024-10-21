package com.manmeet.animalsys.entity;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String fileName;

    @NotBlank
    private String fileType; // e.g., image/jpeg, video/mp4

    @Lob
    @Column(name = "data", columnDefinition = "LONGBLOB")
    private byte[] data; // store the file data

    @ManyToOne
    @JoinColumn(name = "report_id", nullable = false)
    private Report report; // link to the report

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public Attachment(String fileName, String fileType, byte[] data) {
	    this.fileName = fileName;
	    this.fileType = fileType;
	    this.data = data;
	}
    // Getters and Setters...

	public Attachment() {
	}
	
	 // Method to get Resource
    public Resource getResource() {
    	return new ByteArrayResource(data);
    }
}

