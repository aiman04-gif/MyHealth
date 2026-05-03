package com.example.myhealth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class HomeFragment extends Fragment {

    private TextView tvName;
    private TextView textHomeAppointmentDate;
    private TextView textHomeAppointmentDoctor;
    private View emergencyCall,nearbyHospitals, nearbyPharmacy, bookAppointment, upcomingConsultation;
    private CardView cardprofile;


    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        init(v);

        SharedPreferences sp = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String email = sp.getString("user_email", null);
        String name = sp.getString("user_name", "");

        if (!name.isEmpty()) {
            tvName.setText("Hi "+name);
        }
        loadRecentAppointment();

        emergencyCall.setOnClickListener(view->{
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:1122"));
            startActivity(intent);

        });

        bookAppointment.setOnClickListener(view->{
            Navigation.findNavController(view).navigate(R.id.nav_doctors);
        });

        upcomingConsultation.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.nav_appointments);
        });

        nearbyPharmacy.setOnClickListener(view->{
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=pharmacies near me");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });

        nearbyHospitals.setOnClickListener(view->{
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=hospitals near me");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });


        // Inflate the layout for this fragment
        return v;
    }

    private void init(View v) {
        tvName=v.findViewById(R.id.userName);
        textHomeAppointmentDate = v.findViewById(R.id.text_home_appointment_date);
        textHomeAppointmentDoctor = v.findViewById(R.id.text_home_appointment_doctor);
        emergencyCall=v.findViewById(R.id.emergencyCall);
        nearbyHospitals=v.findViewById(R.id.nearbyHospitals);
        nearbyPharmacy=v.findViewById(R.id.nearbyPharmacy);
        bookAppointment=v.findViewById(R.id.bookAppointment);
        upcomingConsultation=v.findViewById(R.id.upcomingConsultation);
        cardprofile=v.findViewById(R.id.cardProfile);

    }

    private void loadRecentAppointment() {
        String patientUid = getPatientUid();
        if (patientUid == null || patientUid.trim().isEmpty()) {
            showNoAppointment();
            return;
        }

        FirebaseDatabase.getInstance().getReference("appointments")
                .orderByChild("patientUid")
                .equalTo(patientUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Appointment latestAppointment = null;
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Appointment appointment = child.getValue(Appointment.class);
                            if (appointment == null) {
                                continue;
                            }
                            if (isCompleted(appointment)) {
                                continue;
                            }
                            if (latestAppointment == null
                                    || appointment.getCreatedAt() >= latestAppointment.getCreatedAt()) {
                                latestAppointment = appointment;
                            }
                        }

                        if (latestAppointment == null) {
                            showNoAppointment();
                            return;
                        }

                        textHomeAppointmentDate.setText(safeText(latestAppointment.getDate(), "Date not set")
                                + " - " + safeText(latestAppointment.getTime(), "Time not set"));
                        textHomeAppointmentDoctor.setText(safeText(latestAppointment.getDoctorName(), "Doctor")
                                + " - " + safeText(latestAppointment.getStatus(), "Pending"));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        showNoAppointment();
                    }
                });
    }

    private String getPatientUid() {
        SharedPreferences sp = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String sessionUid = sp.getString("user_uid", "");
        if (sessionUid != null && !sessionUid.trim().isEmpty()) {
            return sessionUid;
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null ? user.getUid() : "";
    }

    private void showNoAppointment() {
        textHomeAppointmentDate.setText("No appointment");
        textHomeAppointmentDoctor.setText("Book a consultation");
    }

    private String safeText(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }

    private boolean isCompleted(Appointment appointment) {
        String status = appointment.getStatus();
        return "Done".equalsIgnoreCase(status) || "Completed".equalsIgnoreCase(status);
    }
}
