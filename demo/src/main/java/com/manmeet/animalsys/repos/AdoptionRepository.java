package com.manmeet.animalsys.repos;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.manmeet.animalsys.entity.Adoption;
import com.manmeet.animalsys.entity.AdoptionRequestStatus;

@Repository
public interface AdoptionRepository extends JpaRepository<Adoption, Long> {
	

	// Retrieve adoptions by status
    List<Adoption> findByStatus(AdoptionRequestStatus status);

    // Retrieve adoptions by user ID
    List<Adoption> findByUserId(Long userId);

    // Custom query to find adoptions within a date range    
    @Query("SELECT a FROM Adoption a WHERE a.requestDate BETWEEN :startDate AND :endDate")
    List<Adoption> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}

