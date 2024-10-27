package com.manmeet.animalsys.service;

import java.util.List;
import java.util.Optional;

import com.manmeet.animalsys.entity.Animal;
import com.manmeet.animalsys.entity.Attachment;
import com.manmeet.animalsys.entity.Report;
import com.manmeet.animalsys.entity.ReportType;
import com.manmeet.animalsys.entity.User;

public interface ReportService {

	Report submitReport(Report report);

	List<Report> getReportsByUser(User user);

	List<Report> getReportsByType(ReportType type);

	List<Report> getReportsByAnimal(Animal animal);

	List<Attachment> getAttachments(Long reportId);

	Optional<Report> findById(Long id);

	Attachment findAttachmentByIdAndReportId(Long attachmentId, Long reportId);
	// Other methods for managing reports...

	List<Report> findAllReports();

	List<Report> findReportsByType(ReportType type);

	Report approveReport(Long id);


	Report rejectReport(Long id, String rejectionReason);

	List<Report> getPendingReports();

	List<Report> searchReports(String description, String type, String status);

}