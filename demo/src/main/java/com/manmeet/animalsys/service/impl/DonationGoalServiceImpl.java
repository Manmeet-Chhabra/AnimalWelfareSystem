package com.manmeet.animalsys.service.impl;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manmeet.animalsys.entity.DonationGoal;
import com.manmeet.animalsys.repos.DonationGoalRepository;
import com.manmeet.animalsys.service.DonationGoalService;

import jakarta.annotation.PostConstruct;

@Service
public class DonationGoalServiceImpl implements DonationGoalService {

	@Autowired
	private DonationGoalRepository donationGoalRepository;

	@Override
	public void addDonation(BigDecimal amount) {
		DonationGoal goal = donationGoalRepository.findById(1L)
				.orElseThrow(() -> new NoSuchElementException("DonationGoal with ID 1 not found."));
		goal.setCurrentTotal(goal.getCurrentTotal().add(amount));
		donationGoalRepository.save(goal);
	}

	@Override
	public DonationGoal getCurrentGoal() {
		return donationGoalRepository.findById(1L)
				.orElseThrow(() -> new NoSuchElementException("DonationGoal with ID 1 not found."));
	}

	@Override
	@Transactional
	public void setGoal(BigDecimal amount) {
	    DonationGoal goal = donationGoalRepository.findById(1L).orElseGet(() -> {
	        DonationGoal newGoal = new DonationGoal();
	        newGoal.setCurrentTotal(BigDecimal.ZERO);
	        newGoal.setGoalAmount(amount); // Set goal amount here for new goal
	        return newGoal;
	    });
	    
	    goal.setGoalAmount(amount); // Update the goalAmount if it already exists
	    donationGoalRepository.save(goal); // Save changes
	}



	@PostConstruct
	public void init() {
		if (!donationGoalRepository.existsById(1L)) {
			DonationGoal initialGoal = new DonationGoal();
			initialGoal.setId(1L); // Set the ID explicitly
			initialGoal.setCurrentTotal(BigDecimal.ZERO);
			initialGoal.setGoalAmount(BigDecimal.ZERO); // Set initial goal amount if needed
			donationGoalRepository.save(initialGoal);
		}
	}

}
