package com.manmeet.animalsys.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class DonationGoal {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "goal_amount", nullable = false)
	private BigDecimal goalAmount; // Monthly or annual goal

	@Column(name = "current_total", nullable = false, columnDefinition = "decimal(10,2) default '0.00'") // Maps to
																											// current_total
	private BigDecimal currentTotal = BigDecimal.ZERO; // Current donations received

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getGoalAmount() {
		return goalAmount;
	}

	public void setGoalAmount(BigDecimal goalAmount) {
		this.goalAmount = goalAmount;
	}

	public BigDecimal getCurrentTotal() {
		return currentTotal;
	}

	public void setCurrentTotal(BigDecimal currentTotal) {
		this.currentTotal = currentTotal;
	}

	public DonationGoal(Long id, BigDecimal goalAmount, BigDecimal currentTotal) {
		super();
		this.id = id;
		this.goalAmount = goalAmount;
		this.currentTotal = currentTotal;
	}

	public DonationGoal() {
		super();
	}

	// Getters, Setters, and Constructors
}
