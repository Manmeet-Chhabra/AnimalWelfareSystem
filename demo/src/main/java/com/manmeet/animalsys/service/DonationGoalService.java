package com.manmeet.animalsys.service;

import java.math.BigDecimal;

import com.manmeet.animalsys.entity.DonationGoal;

public interface DonationGoalService {

	void addDonation(BigDecimal amount);

	DonationGoal getCurrentGoal();

	void setGoal(BigDecimal amount);
}
