package com.manmeet.animalsys.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.manmeet.animalsys.entity.Donation;
import com.manmeet.animalsys.repos.DonationRepository;
import com.manmeet.animalsys.service.DonationScheduler;

@Service
public class DonationSchedulerImpl implements DonationScheduler {

	@Autowired
	private DonationRepository donationRepository;

	// Run this task on the first day of every month
	@Override
	@Scheduled(cron = "0 0 0 1 * *") // Cron expression for midnight on the first day of the month

	public void processMonthlyDonations() {
		List<Donation> recurringDonations = donationRepository.findByRecurringTrue();

		for (Donation donation : recurringDonations) {
			Donation monthlyDonation = new Donation();
			monthlyDonation.setDonationType(donation.getDonationType());
			monthlyDonation.setAmount(donation.getAmount());
			monthlyDonation.setQuantity(donation.getQuantity());
			monthlyDonation.setDonorName(donation.getDonorName());
			monthlyDonation.setMobileNumber(donation.getMobileNumber());
			monthlyDonation.setEmail(donation.getEmail());
			monthlyDonation.setAddress(donation.getAddress());
			monthlyDonation.setDate(LocalDate.now()); // Set the date to the current date
			monthlyDonation.setRecurring(false); // The new instance is not marked as recurring

			donationRepository.save(monthlyDonation);
		}
	}
}
