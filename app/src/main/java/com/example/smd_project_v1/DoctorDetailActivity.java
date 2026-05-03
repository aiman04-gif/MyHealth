package com.example.smd_project_v1;

import com.example.myhealth.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class DoctorDetailActivity extends AppCompatActivity {

    public static final String EXTRA_DOCTOR_UID = "doctor_uid";
    public static final String EXTRA_DOCTOR_NAME = "doctor_name";
    public static final String EXTRA_DOCTOR_EMAIL = "doctor_email";
    public static final String EXTRA_DOCTOR_SPECIALTY = "doctor_specialty";
    public static final String EXTRA_DOCTOR_SPECIALTIES = "doctor_specialties";
    public static final String EXTRA_DOCTOR_ABOUT = "doctor_about";
    public static final String EXTRA_DOCTOR_EXPERIENCE = "doctor_experience_years";
    public static final String EXTRA_DOCTOR_RATING = "doctor_rating";
    public static final String EXTRA_DOCTOR_REVIEW_COUNT = "doctor_review_count";
    public static final String EXTRA_DOCTOR_FEE = "doctor_fee_usd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_detail);

        View root = findViewById(R.id.root_doctor_detail);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindDoctor(readDoctorFromIntent());
    }

    private DoctorDisplay readDoctorFromIntent() {
        Intent intent = getIntent();
        List<String> tags = intent.getStringArrayListExtra(EXTRA_DOCTOR_SPECIALTIES);
        if (tags == null || tags.isEmpty()) {
            tags = new ArrayList<>();
            tags.add(safeText(intent.getStringExtra(EXTRA_DOCTOR_SPECIALTY), "General Physician"));
        }

        return new DoctorDisplay(
                intent.getStringExtra(EXTRA_DOCTOR_UID),
                safeText(intent.getStringExtra(EXTRA_DOCTOR_NAME), "Doctor"),
                safeText(intent.getStringExtra(EXTRA_DOCTOR_ABOUT), "Doctor details are not available yet."),
                tags,
                intent.getIntExtra(EXTRA_DOCTOR_EXPERIENCE, 0),
                intent.getDoubleExtra(EXTRA_DOCTOR_RATING, 0.0),
                intent.getIntExtra(EXTRA_DOCTOR_REVIEW_COUNT, 0),
                intent.getIntExtra(EXTRA_DOCTOR_FEE, 0)
        );
    }

    private void bindDoctor(DoctorDisplay doctor) {
        TextView name = findViewById(R.id.text_doctor_name);
        TextView subtitle = findViewById(R.id.text_doctor_specialty_exp);
        TextView rating = findViewById(R.id.text_rating);
        TextView about = findViewById(R.id.text_about);
        TextView fee = findViewById(R.id.text_fee);
        LinearLayout chipContainer = findViewById(R.id.container_specializations);

        ImageButton back = findViewById(R.id.button_back);
        ImageButton favorite = findViewById(R.id.button_favorite);
        MaterialButton book = findViewById(R.id.button_book_appointment);

        name.setText(doctor.getName());
        subtitle.setText(doctor.formatSpecialtyWithExperience(" - "));
        rating.setText(doctor.formatRatingLine());
        about.setText(doctor.getAbout());
        fee.setText(doctor.formatFeePerSession("$"));

        populateChipRow(chipContainer, doctor);

        back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        favorite.setOnClickListener(v ->
                Toast.makeText(this, R.string.doctor_detail_favorite_toast, Toast.LENGTH_SHORT).show()
        );
        book.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookAppointmentActivity.class);
            intent.putExtra(EXTRA_DOCTOR_UID, doctor.getUid());
            intent.putExtra(EXTRA_DOCTOR_NAME, doctor.getName());
            intent.putExtra(EXTRA_DOCTOR_SPECIALTY, doctor.getPrimarySpecialty());
            startActivity(intent);
        });
    }

    private void populateChipRow(LinearLayout container, DoctorDisplay doctor) {
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(container.getContext());

        boolean first = true;
        final int gap = (int) (8 * getResources().getDisplayMetrics().density + 0.5f);

        for (String tag : doctor.getSpecializationTags()) {
            TextView chip = (TextView) inflater.inflate(
                    R.layout.item_doctor_specialization_chip,
                    container,
                    false
            );
            chip.setText(tag);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            if (!first) {
                lp.setMarginStart(gap);
            }
            chip.setLayoutParams(lp);
            container.addView(chip);
            first = false;
        }
    }

    private String safeText(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }

    private static class DoctorDisplay {
        private final String uid;
        private final String name;
        private final String about;
        private final List<String> specializationTags;
        private final int experienceYears;
        private final double rating;
        private final int reviewCount;
        private final int consultationFeeUsd;

        DoctorDisplay(String uid, String name, String about, List<String> specializationTags,
                      int experienceYears, double rating, int reviewCount, int consultationFeeUsd) {
            this.uid = uid;
            this.name = name;
            this.about = about;
            this.specializationTags = specializationTags;
            this.experienceYears = experienceYears;
            this.rating = rating;
            this.reviewCount = reviewCount;
            this.consultationFeeUsd = consultationFeeUsd;
        }

        String getUid() {
            return uid;
        }

        String getName() {
            return name;
        }

        String getAbout() {
            return about;
        }

        List<String> getSpecializationTags() {
            return specializationTags;
        }

        String getPrimarySpecialty() {
            return specializationTags.isEmpty() ? "General Physician" : specializationTags.get(0);
        }

        String formatSpecialtyWithExperience(String separator) {
            if (experienceYears <= 0) {
                return getPrimarySpecialty();
            }
            return getPrimarySpecialty() + separator + experienceYears + " Years Exp.";
        }

        String formatRatingLine() {
            if (rating <= 0) {
                return "No reviews yet";
            }
            return rating + " (" + reviewCount + " reviews)";
        }

        String formatFeePerSession(String currencyPrefix) {
            if (consultationFeeUsd <= 0) {
                return "Fee not available";
            }
            return currencyPrefix + consultationFeeUsd + " / session";
        }
    }
}
