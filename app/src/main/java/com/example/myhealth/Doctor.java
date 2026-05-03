package com.example.myhealth;

import java.util.List;

public class Doctor {
    private String uid;
    private String email;
    private String name;
    private int experienceYears;
    private double rating;
    private int reviewCount;
    private List<String> specializationTags;
    private String about;
    private int consultationFeeUsd;

    public Doctor() {}

    public Doctor(String uid, String name, String email,
                  int experienceYears, double rating, int reviewCount,
                  List<String> specializationTags, String about, int consultationFeeUsd) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.experienceYears = experienceYears;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.specializationTags = specializationTags;
        this.about = about;
        this.consultationFeeUsd = consultationFeeUsd;
    }

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
