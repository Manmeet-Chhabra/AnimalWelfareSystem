package com.manmeet.animalsys.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manmeet.animalsys.entity.Animal;
import com.manmeet.animalsys.entity.Attachment;
import com.manmeet.animalsys.entity.Report;
import com.manmeet.animalsys.entity.ReportType;
import com.manmeet.animalsys.entity.User;
import com.manmeet.animalsys.repos.AttachmentRepository;
import com.manmeet.animalsys.repos.ReportRepository;
import com.manmeet.animalsys.service.ReportService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportRepository reportRepository;
    
    @PersistenceContext
    private EntityManager entityManager; // Ensure this is correctly injected

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Override
    @Transactional
    public Report submitReport(Report report) {
        // Validate report object before saving (implement validation logic as needed)
        report.setReportDate(LocalDateTime.now());
        return reportRepository.save(report);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Report> getReportsByUser(User user) {
        return reportRepository.findByReportedBy(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Report> getReportsByType(ReportType type) {
        return reportRepository.findByType(type);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Report> getReportsByAnimal(Animal animal) {
        return reportRepository.findByAnimal(animal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attachment> getAttachments(Long reportId) {
        Optional<Report> report = reportRepository.findById(reportId);
        return report.map(Report::getAttachments).orElse(null); // Return null if report not found
    }
    
    @Override
    public Optional<Report> findById(Long id) {
        return reportRepository.findById(id);
    }
    
    @Override
    public Attachment findAttachmentByIdAndReportId(Long attachmentId, Long reportId) {
        // Fetch the attachment based on the attachmentId and reportId
        return attachmentRepository.findByIdAndReportId(attachmentId, reportId).orElse(null);
    }
    // Other methods for managing reports...
    
    @Override
    public List<Report> findAllReports() {
        return reportRepository.findAll();
    }

    @Override
    public List<Report> findReportsByType(ReportType type) {
        return reportRepository.findByType(type);
    }
    
    @Override
    public Report approveReport(Long id) {
        Optional<Report> reportOptional = reportRepository.findById(id);
        if (reportOptional.isPresent()) {
            Report report = reportOptional.get();
            // Update the report status to approved
            report.setStatus("approved"); // This should now work
            return reportRepository.save(report); // Save the updated report
        }
        throw new EntityNotFoundException("Report not found with id: " + id);
    }
    
    @Override
    public Report rejectReport(Long id, String rejectionReason) {
        Optional<Report> reportOptional = reportRepository.findById(id);
        if (reportOptional.isPresent()) {
            Report report = reportOptional.get();
            report.setStatus("rejected"); // Update the status to rejected
            report.setRejectionReason(rejectionReason); // Set the rejection reason
            return reportRepository.save(report); // Save the updated report
        }
        throw new EntityNotFoundException("Report not found with id: " + id);
    }

    @Override
    public List<Report> getPendingReports() {
        return reportRepository.findByStatus("PENDING");
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Report> searchReports(String description, String type, String status) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Report> cq = cb.createQuery(Report.class);
        Root<Report> report = cq.from(Report.class);
        
        List<Predicate> predicates = new ArrayList<>();

        if (description != null && !description.isEmpty()) {
            predicates.add(cb.like(report.get("description"), "%" + description + "%"));
        }
        if (type != null && !type.isEmpty()) {
            predicates.add(cb.equal(report.get("type"), type));
        }
        if (status != null && !status.isEmpty()) {
            predicates.add(cb.equal(report.get("status"), status));
        }
        
        cq.where(predicates.toArray(new Predicate[0]));
        
        return entityManager.createQuery(cq).getResultList();
    }
    
    

    
}