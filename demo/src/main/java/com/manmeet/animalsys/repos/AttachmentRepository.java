package com.manmeet.animalsys.repos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.manmeet.animalsys.entity.Attachment;
import com.manmeet.animalsys.entity.Report;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

	List<Attachment> findByReportId(Long reportId); // Find attachments by report ID

	List<Attachment> findByReport(Report report); // Find attachments by Report entity

	Optional<Attachment> findByIdAndReportId(Long attachmentId, Long reportId);
}
