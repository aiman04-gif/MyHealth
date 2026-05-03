package com.example.smd_project_v1;

import com.example.myhealth.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class AppointmentsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_appointments);

        View root = findViewById(R.id.root_appointments);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton backButton = findViewById(R.id.button_back);
        TextView appointmentDateTimeText = findViewById(R.id.text_appointment_datetime);
        MaterialButton viewDetailButton = findViewById(R.id.button_view_appointment_detail);
        MaterialButton navStatisticsButton = findViewById(R.id.button_nav_statistics);
        MaterialButton navProfileButton = findViewById(R.id.button_nav_profile);

        updateAppointmentDateTime(appointmentDateTimeText);

        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        viewDetailButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AppointmentDetailActivity.class);
            startActivity(intent);
        });
        navStatisticsButton.setOnClickListener(v ->
                startActivity(new Intent(this, StatisticsActivity.class))
        );
        navProfileButton.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class))
        );
    }

    private void updateAppointmentDateTime(TextView appointmentDateTimeText) {
        Intent intent = getIntent();
        String appointmentDate = intent.getStringExtra("appointment_date");
        String appointmentTime = intent.getStringExtra("appointment_time");

        if (appointmentDate != null && appointmentTime != null) {
            appointmentDateTimeText.setText(appointmentDate + " - " + appointmentTime);
        }
    }
}
