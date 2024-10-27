package com.manmeet.animalsys.entity;

public enum DonationType {
	FOOD("Food"), // quantity
	SUPPLIES("Supplies"), // quantity
	HEALTH_HYGIENE("Health and Hygiene Products"), // money
	TOYS("Toys and Enrichment"), // Pet toys (balls, chew toys, scratching posts) - quantity
	EQUIPMENT("Equipment"), // Collars and leashes, Crates or carriers - quantity
	MONEY("Monetary Donations"), // money
	VOLUNTEER("Volunteer Time"), // Time
	EDUCATIONAL("Educational Materials"), // money
	OTHER("Other"); // quantity

	private final String displayName;

	DonationType(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

}
