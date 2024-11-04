package com.manmeet.animalsys.entity;

public enum AdoptionRequestStatus {

	PENDING, APPROVED, REJECTED;
	
	public static AdoptionRequestStatus fromString(String status) {
	    try {
	        return AdoptionRequestStatus.valueOf(status.toUpperCase());
	    } catch (IllegalArgumentException e) {
	        return null; // or handle the case when the status is invalid
	    }
	}
}
