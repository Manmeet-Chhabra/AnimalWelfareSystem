package com.manmeet.animalsys.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.manmeet.animalsys.entity.Animal;
import com.manmeet.animalsys.entity.Report;
import com.manmeet.animalsys.entity.ReportType;
import com.manmeet.animalsys.entity.User;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

	List<Report> findByType(ReportType type);

	List<Report> findByReportedBy(User user);

	List<Report> findByAnimal(Animal animal);

	List<Report> findByStatus(String status);
	
	/* @Query("SELECT r FROM Report r WHERE "
	         + "(:description IS NULL OR r.description LIKE %:description%) AND "
	         + "(:type IS NULL OR r.type = :type) AND "
	         + "(:status IS NULL OR r.status = :status)")
	    List<Report> findReportsByFilters(@Param("description") String description,
	                                      @Param("type") String type,
	                                      @Param("status") String status);*/
}
