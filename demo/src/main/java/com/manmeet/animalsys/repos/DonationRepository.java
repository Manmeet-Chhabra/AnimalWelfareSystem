package com.manmeet.animalsys.repos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.manmeet.animalsys.entity.Donation;
import com.manmeet.animalsys.entity.DonationType;
import com.manmeet.animalsys.entity.User;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

	// Find donations by donor name or donation type
    List<Donation> findByDonorNameContainingOrDonationTypeContaining(String donorName, String donationType);

    // Sum the donation amounts
    @Query("SELECT SUM(d.amount) FROM Donation d")
    BigDecimal sumAmount();

    // Find top 10 recent donations
    List<Donation> findTop10ByOrderByDateDesc();

    // Find donations by user
    List<Donation> findByUser(User user);

    // Find recurring donations
    List<Donation> findByRecurringTrue();

    // Find donation by donor name, donation type, and date
    Optional<Donation> findByDonorNameAndDonationTypeAndDate(String donorName, DonationType donationType, LocalDate date);

    // Custom query for donations within a specific date range
    @Query("SELECT d FROM Donation d WHERE d.date BETWEEN :startDate AND :endDate")
    List<Donation> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    
}
