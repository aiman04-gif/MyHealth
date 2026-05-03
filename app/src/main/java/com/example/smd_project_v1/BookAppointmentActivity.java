package com.example.smd_project_v1;

import com.example.myhealth.Appointment;
import com.example.myhealth.HomePage;
import com.example.myhealth.R;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BookAppointmentActivity extends AppCompatActivity {

    private Calendar selectedDate;
    private String selectedTime;
    private MaterialButton selectedTimeSlotButton;
    private MaterialButton[] slotButtons;
    private MaterialButton confirmButton;

    private TextView textSelectedDate;
    private String doctorUid;
    private String doctorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_appointment);

        View root = findViewById(R.id.root_book_appointment);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textSelectedDate = findViewById(R.id.text_selected_date);
        LinearLayout datePickArea = findViewById(R.id.date_picker_touch_area);

        MaterialButton btn09 = findViewById(R.id.button_slot_09);
        MaterialButton btn1030 = findViewById(R.id.button_slot_1030);
        MaterialButton btn12 = findViewById(R.id.button_slot_12);
        MaterialButton btn14 = findViewById(R.id.button_slot_14);
        slotButtons = new MaterialButton[]{btn09, btn1030, btn12, btn14};

        ImageButton backButton = findViewById(R.id.button_back);
        confirmButton = findViewById(R.id.button_confirm_booking);
        readDoctorExtras();

        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        datePickArea.setOnClickListener(v -> showDatePickerDialog());

        updateTimeSlotStyles();
        for (MaterialButton btn : slotButtons) {
            btn.setOnClickListener(slot -> {
                selectedTimeSlotButton = (MaterialButton) slot;
                selectedTime = selectedTimeSlotButton.getText().toString();
                updateTimeSlotStyles();
            });
        }

        confirmButton.setOnClickListener(v -> confirmBookingOrToast());
    }

    private void readDoctorExtras() {
        Intent intent = getIntent();
        doctorUid = intent.getStringExtra(DoctorDetailActivity.EXTRA_DOCTOR_UID);
        doctorName = intent.getStringExtra(DoctorDetailActivity.EXTRA_DOCTOR_NAME);
    }

    private void showDatePickerDialog() {
        Calendar initial = selectedDate != null ? (Calendar) selectedDate.clone() : Calendar.getInstance();
        int year = initial.get(Calendar.YEAR);
        int month = initial.get(Calendar.MONTH);
        int day = initial.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, y, monthOfYear, dayOfMonth) -> {
                    if (selectedDate == null) {
                        selectedDate = Calendar.getInstance();
                    }
                    selectedDate.set(Calendar.YEAR, y);
                    selectedDate.set(Calendar.MONTH, monthOfYear);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    SimpleDateFormat fmt = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.US);
                    textSelectedDate.setText(fmt.format(selectedDate.getTime()));
                    textSelectedDate.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
                },
                year,
                month,
                day
        );
        dialog.show();
    }

    private void updateTimeSlotStyles() {
        float density = getResources().getDisplayMetrics().density;
        int strokePx = Math.round(1f * density);
        if (strokePx < 1) {
            strokePx = 1;
        }

        int themePrimary = ContextCompat.getColor(this, R.color.theme_primary);
        int black = ContextCompat.getColor(this, R.color.black);
        int white = ContextCompat.getColor(this, R.color.white);

        for (MaterialButton btn : slotButtons) {
            boolean selected = selectedTimeSlotButton != null && btn == selectedTimeSlotButton;
            if (selected) {
                btn.setStrokeWidth(0);
                btn.setStrokeColor(ColorStateList.valueOf(themePrimary));
                btn.setBackgroundTintList(ColorStateList.valueOf(themePrimary));
                btn.setTextColor(white);
            } else {
                btn.setStrokeWidth(strokePx);
                btn.setStrokeColor(ColorStateList.valueOf(themePrimary));
                btn.setBackgroundTintList(ColorStateList.valueOf(white));
                btn.setTextColor(black);
            }
        }
    }

    private void confirmBookingOrToast() {
        if (selectedDate == null || selectedTimeSlotButton == null) {
            showAlert("Missing selection", getString(R.string.toast_select_date_and_time));
            return;
        }
        if (doctorUid == null || doctorUid.trim().isEmpty()) {
            showAlert("Doctor unavailable", "Please select a doctor again before booking.");
            return;
        }
        saveAppointment();
    }

    private void saveAppointment() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
        String patientUid = firebaseUser != null ? firebaseUser.getUid() : sp.getString("user_uid", "");
        String patientName = sp.getString("user_name", "");

        if (patientUid == null || patientUid.trim().isEmpty()) {
            showAlert("Login required", "Please login again before booking an appointment.");
            return;
        }
        if (patientName == null || patientName.trim().isEmpty()) {
            patientName = firebaseUser != null && firebaseUser.getEmail() != null
                    ? firebaseUser.getEmail()
                    : "Patient";
        }

        SimpleDateFormat fmt = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.US);
        String date = fmt.format(selectedDate.getTime());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("appointments").push();
        String appointmentId = ref.getKey();
        Appointment appointment = new Appointment(
                appointmentId,
                doctorUid,
                doctorName,
                patientUid,
                patientName,
                date,
                selectedTime,
                "Pending",
                System.currentTimeMillis()
        );

        setSaving(true);
        ref.setValue(appointment)
                .addOnSuccessListener(unused -> openAppointmentsTab(date))
                .addOnFailureListener(error -> {
                    setSaving(false);
                    showAlert("Booking failed", error.getMessage() != null
                            ? error.getMessage()
                            : "Unable to create appointment. Please try again.");
                });
    }

    private void openAppointmentsTab(String date) {
        Intent intent = new Intent(this, HomePage.class);
        intent.putExtra("appointment_date", date);
        intent.putExtra("appointment_time", selectedTime);
        intent.putExtra(HomePage.EXTRA_OPEN_TAB, HomePage.TAB_APPOINTMENTS);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void setSaving(boolean saving) {
        confirmButton.setEnabled(!saving);
        confirmButton.setText(saving ? "Booking..." : getString(R.string.confirm_booking));
    }

    private void showAlert(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}
