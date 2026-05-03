package com.example.myhealth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class DoctorLogin extends AppCompatActivity {

    Button btnBacktoPatient;
    TextView tvGotoSignUp;
    EditText etEmail, etPassword;
    FirebaseAuth auth;
    private FirebaseDatabase rtdb;
    AppCompatButton btnDoctorLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_login);
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


        btnBacktoPatient.setOnClickListener(v->{
            startActivity(new Intent(DoctorLogin.this,Login.class));
            finish();
        });
        tvGotoSignUp.setOnClickListener(v->{
            startActivity(new Intent(DoctorLogin.this,DoctorSignUp.class));
            finish();
        });

        btnDoctorLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty()) {
                etEmail.setError("Enter Email");
            } else if (password.isEmpty()) {
                etPassword.setError("Enter Password");
            } else {
                // 1. Authenticate with Firebase Auth
                auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(result -> {
                            String uid = result.getUser().getUid();

                            // 2. IMPORTANT: Check the "doctors" node, not the "users" node
                            rtdb.getReference("doctors")
                                    .child(uid)
                                    .get()
                                    .addOnSuccessListener(snapshot -> {

                                        if (!snapshot.exists()) {
                                            // This triggers if a Patient tries to login here
                                            auth.signOut(); // Log them out immediately
                                            Toast.makeText(this, "Access Denied: Not a Doctor account", Toast.LENGTH_LONG).show();
                                            return;
                                        }

                                        // 3. Get Doctor specific data
                                        String name = snapshot.child("name").getValue(String.class);
                                        String specialty = snapshot.child("specializationTags").child("0").getValue(String.class);

                                        // 4. Save Doctor Session
                                        SharedPreferences sp = getSharedPreferences("doctor_session", MODE_PRIVATE);
                                        sp.edit()
                                                .putBoolean("is_doctor", true)
                                                .putString("doc_uid", uid)
                                                .putString("doc_email", email)
                                                .putString("doc_name", name)
                                                .putString("doc_specialty", specialty)
                                                .apply();

                                        // 5. Redirect to Doctor Dashboard
                                        startActivity(new Intent(DoctorLogin.this, DoctorDashboard.class));
                                        finish();

                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Database Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });

                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Login Failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                        );
            }
        });

    }

    private void init()
    {
        btnBacktoPatient=findViewById(R.id.btnBackToPatient);
        tvGotoSignUp=findViewById(R.id.tvGoToSignup);
        btnDoctorLogin=findViewById(R.id.btnDoctorLogin);
        etEmail=findViewById(R.id.etEmail);
        etPassword=findViewById(R.id.etDoctorPassword);
    }
}