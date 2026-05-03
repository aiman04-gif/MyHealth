package com.example.myhealth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class DoctorSignUp extends AppCompatActivity {

    Button btnBacktoLogin;
    EditText etDocName, etDocSpeciality, etDocEmail, etDocPass;
    AppCompatButton btnDoSignup;
    FirebaseAuth auth;
    FirebaseDatabase rtdb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
        auth=FirebaseAuth.getInstance();
        rtdb=FirebaseDatabase.getInstance(
                "https://myhealth-b7de0-default-rtdb.firebaseio.com/"
        );


        btnBacktoLogin.setOnClickListener(v->{
            startActivity(new Intent(DoctorSignUp.this, DoctorLogin.class));
            finish();
        });

        btnDoSignup.setOnClickListener(v -> {
            // 1. Get data from the UI
            String name = etDocName.getText().toString().trim();
            String specialty = etDocSpeciality.getText().toString().trim();
            String email = etDocEmail.getText().toString().trim();
            String password = etDocPass.getText().toString().trim();

            // 2. Validation
            if (name.isEmpty()) {
                etDocName.setError("Name is required");
                return;
            }
            if (specialty.isEmpty()) {
                etDocSpeciality.setError("Specialization is required");
                return;
            }
            if (email.isEmpty()) {
                etDocEmail.setError("Email is required");
                return;
            }
            if (password.length() < 6) {
                etDocPass.setError("Password must be at least 6 characters");
                return;
            }

            // 3. Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(result -> {
                String uid = result.getUser().getUid();

                // Prepare the professional fields with default values
                List<String> tags = new ArrayList<>();
                tags.add(specialty);

                // Create the object using the fields from your model
                Doctor newDoctor = new Doctor(
                        uid,
                        name,
                        email,
                        0,      // experienceYears (Initial)
                        5.0,    // rating (Starting point)
                        0,      // reviewCount (No reviews yet)
                        tags,   // specializationTags (Uses the specialty they typed)
                        "Hello, I am Dr. " + name + ", a specialist in " + specialty + ".", // Default About
                        100     // consultationFeeUsd (Default placeholder)
                );

                // 4. Save to "doctors" node
                rtdb.getReference("doctors")
                        .child(uid)
                        .setValue(newDoctor)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Doctor Account Created!", Toast.LENGTH_SHORT).show();
                            // Redirect to Doctor Login or Dashboard
                            startActivity(new Intent(this, DoctorLogin.class));
                            finish();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Database Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());

            }).addOnFailureListener(e ->
                    Toast.makeText(this, "Authentication Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
        });

    }

    private void init()
    {
        btnBacktoLogin=findViewById(R.id.btnBackToLogin);
        etDocName=findViewById(R.id.etDocName);
        etDocEmail=findViewById(R.id.etDocEmail);
        etDocSpeciality=findViewById(R.id.etDocSpecialty);
        etDocPass=findViewById(R.id.etDocPassword);
        btnDoSignup=findViewById(R.id.btnDoSignUp);
    }
}