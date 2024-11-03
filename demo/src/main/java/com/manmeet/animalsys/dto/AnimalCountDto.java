package com.manmeet.animalsys.dto;

public class AnimalCountDto {
    private String type;
    private Long count;

    public AnimalCountDto(String type, Long count) {
        this.type = type;
        this.count = count;
    }

    // Getters and setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}