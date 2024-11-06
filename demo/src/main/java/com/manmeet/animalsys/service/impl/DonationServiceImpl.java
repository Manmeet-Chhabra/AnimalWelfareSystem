package com.manmeet.animalsys.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.manmeet.animalsys.entity.Donation;
import com.manmeet.animalsys.entity.User;
import com.manmeet.animalsys.repos.DonationRepository;
import com.manmeet.animalsys.service.DonationService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class DonationServiceImpl implements DonationService {
	@Autowired
	private DonationRepository donationRepository;

	@Override
	public void saveDonation(Donation donation) {
		// Check for duplicates based on donor name, donation type, and date
		Optional<Donation> existingDonation = donationRepository.findByDonorNameAndDonationTypeAndDate(
				donation.getDonorName(), donation.getDonationType(), donation.getDate());

		if (existingDonation.isPresent()) {
			// Handle the case where a duplicate donation exists
			throw new IllegalArgumentException("Duplicate donation detected for this donor on the same date.");
		}

		donationRepository.save(donation);
	}

	@Override
	public List<Donation> getAllDonations() {
		return donationRepository.findAll();
	}

	@Override
	public void deleteDonation(Long id) {
		donationRepository.deleteById(id);
	}

	@Override
	public List<Donation> searchDonations(String search) {
		// Implement search logic based on donor name or donation type
		return donationRepository.findByDonorNameContainingOrDonationTypeContaining(search, search);
	}

	@Override
	public int getTotalDonations() {
		return (int) donationRepository.count();
	}

	@Override
	public BigDecimal getTotalAmountDonated() {
		return donationRepository.sumAmount();
	}

	@Override
	// public List<Donation> getRecentDonations() {
	// return donationRepository.findTop10ByOrderByDateDesc();
	// }

	public List<Donation> getRecentDonations() {
		List<Donation> donations = donationRepository.findTop10ByOrderByDateDesc();
		donations.forEach(donation -> {
			System.out.println("Donation Type: " + donation.getDonationType());
			System.out.println("Amount: " + donation.getAmount());
		});
		return donations;
	}

	@Override
	public List<Donation> findDonationsByUser(User user) {
		return donationRepository.findByUser(user); // Pass the user object
	}

	@Override
	public void scheduleMonthlyDonation(Donation donation) {
		donation.setRecurring(true); // Mark this donation as recurring
		donationRepository.save(donation);
	}

	@Override
	public Donation findById(Long id) {
		return donationRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Donation not found with ID " + id));
	}

	// Extract the donor names from the recent donations
	@Override
	public List<Donation> getRecentDonors() {
		// Get the top 10 most recent donations by date, including all details
		return donationRepository.findTop10ByOrderByDateDesc();
	}
}
