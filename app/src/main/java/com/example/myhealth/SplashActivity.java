package com.example.myhealth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY_MS = 1200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(this::openNextScreen, SPLASH_DELAY_MS);
    }

    private void openNextScreen() {
        Intent intent;
        if (isUserLoggedIn()) {
            intent = new Intent(SplashActivity.this, HomePage.class);
        } else {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private boolean isUserLoggedIn() {
        SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
        return FirebaseAuth.getInstance().getCurrentUser() != null
                || sp.getBoolean("logged_in", false);
    }
}
