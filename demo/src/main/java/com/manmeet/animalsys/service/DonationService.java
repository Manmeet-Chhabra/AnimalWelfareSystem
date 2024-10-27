package com.manmeet.animalsys.service;

import java.math.BigDecimal;
import java.util.List;

import com.manmeet.animalsys.entity.Donation;

public interface DonationService {

	void saveDonation(Donation donation);

	List<Donation> getAllDonations();

	void deleteDonation(Long id);

	List<Donation> searchDonations(String search);

	int getTotalDonations();

	BigDecimal getTotalAmountDonated();
}
