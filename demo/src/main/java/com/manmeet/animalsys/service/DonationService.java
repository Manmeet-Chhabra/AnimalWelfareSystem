package com.manmeet.animalsys.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.manmeet.animalsys.entity.Donation;
import com.manmeet.animalsys.entity.User;

public interface DonationService {

	void saveDonation(Donation donation);

	List<Donation> getAllDonations();

	void deleteDonation(Long id);

	List<Donation> searchDonations(String search);

	int getTotalDonations();

	BigDecimal getTotalAmountDonated();

	List<Donation> getRecentDonations();


	void scheduleMonthlyDonation(Donation donation);

	List<Donation> findDonationsByUser(User user);
}
