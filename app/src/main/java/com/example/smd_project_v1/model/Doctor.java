package com.example.smd_project_v1.model;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Immutable doctor profile for UI (dummy/static data).
 */
public final class Doctor {

    private final String name;
    private final String specialty;
    private final int experienceYears;
    private final double rating;
    private final int reviewCount;
    private final List<String> specializationTags;
    private final String about;
    private final int consultationFeeUsd;

    public Doctor(
            String name,
            String specialty,
            int experienceYears,
            double rating,
            int reviewCount,
            List<String> specializationTags,
            String about,
            int consultationFeeUsd
    ) {
        this.name = name;
        this.specialty = specialty;
        this.experienceYears = experienceYears;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.specializationTags = specializationTags != null ? specializationTags : Collections.emptyList();
        this.about = about;
        this.consultationFeeUsd = consultationFeeUsd;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getSpecialty() {
        return specialty;
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

    @NonNull
    public List<String> getSpecializationTags() {
        return specializationTags;
    }

    @NonNull
    public String getAbout() {
        return about;
    }

    public int getConsultationFeeUsd() {
        return consultationFeeUsd;
    }

    @NonNull
    public String formatSpecialtyWithExperience(@NonNull String bullet) {
        return specialty + bullet + experienceYears + " Years Exp.";
    }

    @NonNull
    public String formatRatingLine() {
        return String.format(
                Locale.US,
                "%.1f (%d reviews)",
                rating,
                reviewCount
        );
    }

    @NonNull
    public String formatFeePerSession(@NonNull String currencyPrefix) {
        return currencyPrefix + consultationFeeUsd + " / session";
    }

    /**
     * Matches the Doctor Detail mock from the design board.
     */
    @NonNull
    public static Doctor sampleDetailDoctor() {
        return new Doctor(
                "Dr. Ali Khan",
                "Cardiologist",
                8,
                4.8,
                128,
                Arrays.asList("Heart Specialist", "Adult Cardiology", "ECG"),
                "Board-certified cardiologist focused on preventive care, adult heart disease, and "
                        + "long-term cardiac management. Comfortable with routine checkups as well "
                        + "as follow-ups after procedures and hospital discharge.",
                300
        );
    }
}
