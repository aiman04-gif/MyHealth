package com.example.myhealth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.smd_project_v1.AppointmentDetailActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AppointmentFragment extends Fragment {

    private LinearLayout appointmentContainer;
    private TextView emptyState;

    public AppointmentFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointment, container, false);
        appointmentContainer = view.findViewById(R.id.container_patient_appointments);
        emptyState = view.findViewById(R.id.text_empty_appointments);
        loadPatientAppointments();
        return view;
    }

    private void loadPatientAppointments() {
        String patientUid = getPatientUid();
        if (patientUid == null || patientUid.trim().isEmpty()) {
            showEmpty("Please login to view appointments");
            return;
        }

        FirebaseDatabase.getInstance().getReference("appointments")
                .orderByChild("patientUid")
                .equalTo(patientUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        appointmentContainer.removeAllViews();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Appointment appointment = child.getValue(Appointment.class);
                            if (appointment != null) {
                                appointmentContainer.addView(createAppointmentCard(appointment));
                            }
                        }
                        boolean hasAppointments = appointmentContainer.getChildCount() > 0;
                        emptyState.setVisibility(hasAppointments ? View.GONE : View.VISIBLE);
                        emptyState.setText("No appointments yet");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        showEmpty("Unable to load appointments");
                    }
                });
    }

    private String getPatientUid() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        }
        SharedPreferences sp = requireActivity()
                .getSharedPreferences("user_session", Context.MODE_PRIVATE);
        return sp.getString("user_uid", "");
    }

    private View createAppointmentCard(Appointment appointment) {
        Context context = requireContext();
        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(20), dp(18), dp(20), dp(18));
        card.setBackgroundResource(R.drawable.white_bg);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, dp(16));
        card.setLayoutParams(cardParams);

        TextView doctorName = makeText(safeText(appointment.getDoctorName(), "Doctor"), 20, true);
        TextView dateTime = makeText(
                safeText(appointment.getDate(), "Date not set") + " - " + safeText(appointment.getTime(), "Time not set"),
                15,
                false
        );
        TextView status = makeText("Status: " + safeText(appointment.getStatus(), "Pending"), 15, false);

        MaterialButton detailButton = new MaterialButton(context);
        detailButton.setText("View Details");
        detailButton.setTextColor(ContextCompat.getColor(context, R.color.white));
        detailButton.setTextSize(14);
        detailButton.setAllCaps(false);
        detailButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.theme_primary)
        ));
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.setMargins(0, dp(14), 0, 0);
        detailButton.setLayoutParams(buttonParams);
        detailButton.setOnClickListener(v -> startActivity(
                new Intent(requireContext(), AppointmentDetailActivity.class)
        ));

        card.addView(doctorName);
        card.addView(dateTime);
        card.addView(status);
        card.addView(detailButton);
        return card;
    }

    private TextView makeText(String text, int sp, boolean bold) {
        TextView textView = new TextView(requireContext());
        textView.setText(text);
        textView.setTextSize(sp);
        textView.setTextColor(ContextCompat.getColor(requireContext(), bold ? R.color.black : R.color.text_secondary));
        if (bold) {
            textView.setTypeface(textView.getTypeface(), android.graphics.Typeface.BOLD);
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, bold ? 0 : dp(6), 0, 0);
        textView.setLayoutParams(params);
        return textView;
    }

    private void showEmpty(String message) {
        appointmentContainer.removeAllViews();
        emptyState.setText(message);
        emptyState.setVisibility(View.VISIBLE);
    }

    private String safeText(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
