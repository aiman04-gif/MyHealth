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

import com.example.smd_project_v1.model.Doctor;
import com.google.android.material.button.MaterialButton;

public class DoctorDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_detail);

        Doctor doctor = Doctor.sampleDetailDoctor();

        View root = findViewById(R.id.root_doctor_detail);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );
            return insets;
        });

        bindDoctor(doctor);
    }

    private void bindDoctor(Doctor doctor) {
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
        subtitle.setText(doctor.formatSpecialtyWithExperience(" • "));
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
            startActivity(intent);
        });
    }

    private void populateChipRow(LinearLayout container, Doctor doctor) {
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
}
