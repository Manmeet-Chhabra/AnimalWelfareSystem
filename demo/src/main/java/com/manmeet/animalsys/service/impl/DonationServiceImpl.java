package com.manmeet.animalsys.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.manmeet.animalsys.entity.Donation;
import com.manmeet.animalsys.repos.DonationRepository;
import com.manmeet.animalsys.service.DonationService;

@Service
public class DonationServiceImpl implements DonationService {
	@Autowired
	private DonationRepository donationRepository;

	@Override
	public void saveDonation(Donation donation) {
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

	// Implement the export logic in this method
}
