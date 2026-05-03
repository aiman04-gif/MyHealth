package com.example.myhealth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.smd_project_v1.AppointmentDetailActivity;
import com.google.android.material.button.MaterialButton;

public class AppointmentFragment extends Fragment {

    public AppointmentFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointment, container, false);

        TextView appointmentDateTimeText = view.findViewById(R.id.text_appointment_datetime);
        MaterialButton viewDetailButton = view.findViewById(R.id.button_view_appointment_detail);

        updateAppointmentDateTime(appointmentDateTimeText);
        viewDetailButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AppointmentDetailActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void updateAppointmentDateTime(TextView appointmentDateTimeText) {
        if (requireActivity().getIntent() == null) {
            return;
        }

        String appointmentDate = requireActivity().getIntent().getStringExtra("appointment_date");
        String appointmentTime = requireActivity().getIntent().getStringExtra("appointment_time");

        if (appointmentDate != null && appointmentTime != null) {
            appointmentDateTimeText.setText(appointmentDate + " - " + appointmentTime);
        }
    }
}
