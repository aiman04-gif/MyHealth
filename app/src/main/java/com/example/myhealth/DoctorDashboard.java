package com.example.myhealth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DoctorDashboard extends AppCompatActivity {

    private TextView tvWelcome, tvTodayCount, tvRating;
    private RecyclerView rvAppointments;
    private SharedPreferences sp;
    private List<Appointment> appointmentList;
    private AppointmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_dashboard);

        // 1. Init Views
        tvWelcome = findViewById(R.id.tvWelcomeDoc);
        tvRating = findViewById(R.id.tvRating);
        rvAppointments = findViewById(R.id.rvAppointments);
        ImageView btnProfile = findViewById(R.id.btnProfileSettings);
        tvTodayCount=findViewById(R.id.tvTodayCount);

        // 2. Load Session Data
        sp = getSharedPreferences("doctor_session", MODE_PRIVATE);
        String name = sp.getString("doc_name", "Doctor");
        tvWelcome.setText("Welcome, Dr. " + name);

        // 3. Profile Navigation
        btnProfile.setOnClickListener(v -> {
            // Open the Profile Edit screen
            Intent intent = new Intent(this, DoctorProfile.class);
            startActivity(intent);
        });

        // 4. Load Dynamic Data
        loadDoctorStats();
        setupAppointmentsList();
    }

    private void loadDoctorStats() {
        String uid = sp.getString("doc_uid", "");
        FirebaseDatabase.getInstance().getReference("doctors").child(uid)
                .get().addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        Double rating = snapshot.child("rating").getValue(Double.class);
                        tvRating.setText(String.valueOf(rating != null ? rating : 5.0));
                    }
                });
    }



    private void setupAppointmentsList() {
        rvAppointments = findViewById(R.id.rvAppointments);
        rvAppointments.setLayoutManager(new LinearLayoutManager(this));

        appointmentList = new ArrayList<>();
        adapter = new AppointmentAdapter(appointmentList);
        rvAppointments.setAdapter(adapter);

        String docUid = sp.getString("doc_uid", "");

        // Fetch only appointments for THIS doctor
        FirebaseDatabase.getInstance().getReference("appointments")
                .orderByChild("doctorUid").equalTo(docUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        appointmentList.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Appointment appt = data.getValue(Appointment.class);
                            appointmentList.add(appt);
                        }
                        adapter.notifyDataSetChanged();

                        // Update the "Today" count card dynamically
                        tvTodayCount.setText(String.valueOf(appointmentList.size()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}