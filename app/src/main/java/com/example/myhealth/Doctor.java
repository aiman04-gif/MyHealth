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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public List<String> getSpecializationTags() {
        return specializationTags;
    }

    public void setSpecializationTags(List<String> specializationTags) {
        this.specializationTags = specializationTags;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public int getConsultationFeeUsd() {
        return consultationFeeUsd;
    }

    public void setConsultationFeeUsd(int consultationFeeUsd) {
        this.consultationFeeUsd = consultationFeeUsd;
    }
}
