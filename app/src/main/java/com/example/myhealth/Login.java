package com.example.myhealth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseDatabase rtdb;
    private EditText etEmail, etPassword;
    private TextView tv_signup, textLoginButton;
    private View btn_login;
    private View progressLogin;

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
        if (isUserLoggedIn()) {
            openHomePage();
            return;
        }
        rtdb=FirebaseDatabase.getInstance(
                "https://myhealth-b7de0-default-rtdb.firebaseio.com/"
        );

        btn_login.setOnClickListener(v->{
            String email=etEmail.getText().toString().trim();
            String password=etPassword.getText().toString().trim();

            if(email.isEmpty()){
                showAlert(R.string.validation_title, "Enter Email");
            }else if(password.isEmpty())
            {
                showAlert(R.string.validation_title, "Enter Password");
            }else{
                setLoading(true);
                auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(result -> {

                            String uid = result.getUser().getUid();

                            // now get user role from realtime DB
                            rtdb.getReference("users")
                                    .child(uid)
                                    .get()
                                    .addOnSuccessListener(snapshot -> {

                                        if (!snapshot.exists()) {
                                            setLoading(false);
                                            showAlert(R.string.error_title, "User not found!");
                                            return;
                                        }

                                        String name = snapshot.child("name").getValue(String.class);
                                        saveUserSession(uid, email, name);
                                        openHomePage();

                                    })
                                    .addOnFailureListener(e -> {
                                        setLoading(false);
                                        showAlert(R.string.error_title, "Error: " + e.getMessage());
                                    });

                        })
                        .addOnFailureListener(e -> {
                            setLoading(false);
                            showAlert(R.string.error_title, e.getMessage());
                        });
            }
        });


        tv_signup.setOnClickListener(v->{
            Intent i = new Intent(Login.this, SignUp.class);
            startActivity(i);
        });
    }

    private void init()
    {
        tv_signup= findViewById(R.id.tv_signup);
        etEmail=findViewById(R.id.etEmail);
        etPassword=findViewById(R.id.etPassword);
        btn_login= findViewById(R.id.btn_login);
        textLoginButton= findViewById(R.id.text_login_button);
        progressLogin= findViewById(R.id.progress_login);
    }

    private boolean isUserLoggedIn() {
        SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
        return auth.getCurrentUser() != null || sp.getBoolean("logged_in", false);
    }

    private void saveUserSession(String uid, String email, String name) {
        SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
        sp.edit()
                .putBoolean("logged_in", true)
                .putString("user_uid", uid)
                .putString("user_email", email)
                .putString("user_name", name)
                .apply();
    }

    private void openHomePage() {
        Intent intent = new Intent(Login.this, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean loading) {
        btn_login.setEnabled(!loading);
        tv_signup.setEnabled(!loading);
        etEmail.setEnabled(!loading);
        etPassword.setEnabled(!loading);
        textLoginButton.setVisibility(loading ? View.GONE : View.VISIBLE);
        progressLogin.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void showAlert(int titleResId, String message) {
        new AlertDialog.Builder(this)
                .setTitle(titleResId)
                .setMessage(message != null ? message : getString(R.string.error_title))
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                .show();
    }
}
