package com.example.smd_project_v1;

import com.example.myhealth.R;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BookAppointmentActivity extends AppCompatActivity {

    private Calendar selectedDate;
    private String selectedTime;
    private MaterialButton selectedTimeSlotButton;
    private MaterialButton[] slotButtons;

    private TextView textSelectedDate;

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
        MaterialButton confirmButton = findViewById(R.id.button_confirm_booking);

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

        int black = ContextCompat.getColor(this, R.color.black);
        int white = ContextCompat.getColor(this, R.color.white);

        for (MaterialButton btn : slotButtons) {
            boolean selected = selectedTimeSlotButton != null && btn == selectedTimeSlotButton;
            if (selected) {
                btn.setStrokeWidth(0);
                btn.setStrokeColor(ColorStateList.valueOf(black));
                btn.setBackgroundTintList(ColorStateList.valueOf(black));
                btn.setTextColor(white);
            } else {
                btn.setStrokeWidth(strokePx);
                btn.setStrokeColor(ColorStateList.valueOf(black));
                btn.setBackgroundTintList(ColorStateList.valueOf(white));
                btn.setTextColor(black);
            }
        }
    }

    private void confirmBookingOrToast() {
        if (selectedDate == null || selectedTimeSlotButton == null) {
            Toast.makeText(this, R.string.toast_select_date_and_time, Toast.LENGTH_SHORT).show();
            return;
        }
        openAppointmentsActivity();
    }

    private void openAppointmentsActivity() {
        SimpleDateFormat fmt = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.US);
        Intent intent = new Intent(this, AppointmentsActivity.class);
        intent.putExtra("appointment_date", fmt.format(selectedDate.getTime()));
        intent.putExtra("appointment_time", selectedTime);
        startActivity(intent);
    }
}
