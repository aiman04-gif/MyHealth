package com.example.myhealth;

import java.util.List;

public class Doctor {
    private String name;
    private final int experienceYears;
    private final double rating;
    private final int reviewCount;
    private final List<String> specializationTags;
    private final String about;
    private final int consultationFeeUsd;


    public Doctor(String name, int experienceYears, double rating, int reviewCount, List<String> specializationTags, String about, int consultationFeeUsd) {
        this.name = name;
        this.experienceYears = experienceYears;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.specializationTags = specializationTags;
        this.about = about;
        this.consultationFeeUsd = consultationFeeUsd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public double getRating() {
        return rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public List<String> getSpecializationTags() {
        return specializationTags;
    }

    public String getAbout() {
        return about;
    }

    public int getConsultationFeeUsd() {
        return consultationFeeUsd;
    }
}
