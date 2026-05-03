package com.example.smd_project_v1;

import com.example.myhealth.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int doctorDetailButtonId = getResources().getIdentifier(
                "button_open_doctor_detail",
                "id",
                getPackageName()
        );
        View openDoctorDetail = findViewById(doctorDetailButtonId);
        if (openDoctorDetail != null) {
            openDoctorDetail.setOnClickListener(v -> {
                Intent intent = new Intent(this, DoctorDetailActivity.class);
                startActivity(intent);
            });
        }
    }
}
