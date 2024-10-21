package com.manmeet.animalsys.entity;


public enum ReportType {
    ABUSE("Animal Abuse"),
    ACCIDENT("Animal Accident");

    private final String displayName;

    ReportType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
