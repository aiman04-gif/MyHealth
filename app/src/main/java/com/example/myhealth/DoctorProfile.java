package com.example.myhealth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import android.widget.Toast;

public class DoctorProfile extends AppCompatActivity {

    private EditText etExp, etFee, etAbout;
    private Button btnSave, btnLogout;
    private DatabaseReference docRef;
    private String docUid;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

        // 1. Initialize
        etExp = findViewById(R.id.etProfileExp);
        etFee = findViewById(R.id.etProfileFee);
        etAbout = findViewById(R.id.etProfileAbout);
        btnSave = findViewById(R.id.btnSaveProfile);
        btnLogout=findViewById(R.id.btnLogout);
        auth=FirebaseAuth.getInstance();

        SharedPreferences sp = getSharedPreferences("doctor_session", MODE_PRIVATE);
        docUid = sp.getString("doc_uid", "");
        docRef = FirebaseDatabase.getInstance().getReference("doctors").child(docUid);

        // 2. Load Existing Data
        loadCurrentData();

        // 3. Save Logic
        btnSave.setOnClickListener(v -> saveProfileUpdates());
        btnLogout.setOnClickListener(v -> {
            // Sign out from Firebase
            auth.signOut();

            // Clear SharedPreferences
            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.apply();

            // Redirect to Login Screen (replace DoctorLogin.class with your login activity name)
            Intent intent = new Intent(DoctorProfile.this, DoctorLogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadCurrentData() {
        docRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                etExp.setText(String.valueOf(snapshot.child("experienceYears").getValue(Integer.class)));
                etFee.setText(String.valueOf(snapshot.child("consultationFeeUsd").getValue(Integer.class)));
                etAbout.setText(snapshot.child("about").getValue(String.class));
            }
        });
    }

    private void saveProfileUpdates() {
        String about = etAbout.getText().toString().trim();
        int exp = Integer.parseInt(etExp.getText().toString().trim());
        int fee = Integer.parseInt(etFee.getText().toString().trim());

        HashMap<String, Object> updates = new HashMap<>();
        updates.put("about", about);
        updates.put("experienceYears", exp);
        updates.put("consultationFeeUsd", fee);

        docRef.updateChildren(updates).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            finish(); // Go back to dashboard
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}