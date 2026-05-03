package com.example.myhealth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseDatabase rtdb;
    private EditText etEmail, etPassword;
    private TextView tv_signup;
    private View btn_login;
    private Button btnGotoDoctorLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        auth= FirebaseAuth.getInstance();
        rtdb=FirebaseDatabase.getInstance(
                "https://myhealth-b7de0-default-rtdb.firebaseio.com/"
        );

        btn_login.setOnClickListener(v->{
            String email=etEmail.getText().toString().trim();
            String password=etPassword.getText().toString().trim();

            if(email.isEmpty()){
                etEmail.setError("Enter Email");
            }else if(password.isEmpty())
            {
                etPassword.setError("Enter Password");
            }else{
                auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(result -> {

                            String uid = result.getUser().getUid();

                            // now get user role from realtime DB
                            rtdb.getReference("users")
                                    .child(uid)
                                    .get()
                                    .addOnSuccessListener(snapshot -> {

                                        if (!snapshot.exists()) {
                                            Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        String name = snapshot.child("name").getValue(String.class);
                                        // Save session
                                        SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
                                        sp.edit()
                                                .putBoolean("logged_in", true)
                                                .putString("user_uid", uid)
                                                .putString("user_email", email)
                                                .putString("user_name", name)
                                                .apply();



                                        startActivity(new Intent(Login.this, HomePage.class));
                                        finish();

                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });

                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show()
                        );
            }
        });


        tv_signup.setOnClickListener(v->{
            Intent i = new Intent(Login.this, SignUp.class);
            startActivity(i);
        });
        btnGotoDoctorLogin.setOnClickListener(v->{
            startActivity(new Intent(Login.this,DoctorLogin.class));
        });
    }

    private void init()
    {
        tv_signup= findViewById(R.id.tv_signup);
        etEmail=findViewById(R.id.etEmail);
        etPassword=findViewById(R.id.etPassword);
        btn_login= findViewById(R.id.btn_login);
        btnGotoDoctorLogin=findViewById(R.id.btnGoToDoctorLogin);
    }
}