package com.manmeet.animalsys.repos;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.manmeet.animalsys.entity.Donation;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

	// Custom query to find donations by donor name or donation type
    List<Donation> findByDonorNameContainingOrDonationTypeContaining(String donorName, String donationType);

    // Custom query to sum the donation amounts
    @Query("SELECT SUM(d.amount) FROM Donation d")
    BigDecimal sumAmount();
}
