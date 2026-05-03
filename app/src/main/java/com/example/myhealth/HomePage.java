package com.example.myhealth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomePage extends AppCompatActivity {

    public static final String EXTRA_OPEN_TAB = "open_tab";
    public static final String TAB_APPOINTMENTS = "appointments";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isUserLoggedIn()) {
            openLogin();
            return;
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        BottomNavigationView bottomNav=findViewById(R.id.bottom_navigation);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);

        if (navHostFragment != null) {
            // 3. Get the NavController from the host
            NavController navController = navHostFragment.getNavController();

            setupBottomNavigation(bottomNav, navController);
            setupBackToHome(bottomNav, navController);
            openRequestedTab(bottomNav);
        }

    }

    private void setupBottomNavigation(BottomNavigationView bottomNav, NavController navController) {
        bottomNav.setOnItemSelectedListener(item -> {
            int destinationId = item.getItemId();
            if (navController.getCurrentDestination() != null
                    && navController.getCurrentDestination().getId() == destinationId) {
                return true;
            }

            NavOptions options = new NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setRestoreState(true)
                    .setPopUpTo(navController.getGraph().getStartDestinationId(), false, true)
                    .build();
            navController.navigate(destinationId, null, options);
            return true;
        });

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (bottomNav.getSelectedItemId() != destination.getId()) {
                bottomNav.getMenu().findItem(destination.getId()).setChecked(true);
            }
        });
    }

    private void setupBackToHome(BottomNavigationView bottomNav, NavController navController) {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (navController.getCurrentDestination() != null
                        && navController.getCurrentDestination().getId() != R.id.nav_home) {
                    bottomNav.setSelectedItemId(R.id.nav_home);
                    return;
                }
                setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

    private void openRequestedTab(BottomNavigationView bottomNav) {
        String requestedTab = getIntent().getStringExtra(EXTRA_OPEN_TAB);
        if (TAB_APPOINTMENTS.equals(requestedTab)) {
            bottomNav.post(() -> bottomNav.setSelectedItemId(R.id.nav_appointments));
        }
    }

    private boolean isUserLoggedIn() {
        SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
        return FirebaseAuth.getInstance().getCurrentUser() != null
                || sp.getBoolean("logged_in", false);
    }

    private void openLogin() {
        Intent intent = new Intent(HomePage.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
