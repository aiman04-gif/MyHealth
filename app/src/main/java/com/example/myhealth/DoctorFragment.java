package com.example.myhealth;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.smd_project_v1.DoctorDetailActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;


public class DoctorFragment extends Fragment {

    private DoctorAdapter adapter;
    private TextView emptyState;


    public DoctorFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor, container, false);

        RecyclerView rv = view.findViewById(R.id.rvDoctors);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        emptyState = view.findViewById(R.id.text_empty_doctors);

        // Set the adapter
        adapter = new DoctorAdapter(new ArrayList<>(), doctor -> {
            Intent intent = new Intent(requireContext(), DoctorDetailActivity.class);
            intent.putExtra(DoctorDetailActivity.EXTRA_DOCTOR_UID, doctor.getUid());
            intent.putExtra(DoctorDetailActivity.EXTRA_DOCTOR_NAME, doctor.getName());
            intent.putExtra(DoctorDetailActivity.EXTRA_DOCTOR_EMAIL, doctor.getEmail());
            intent.putExtra(DoctorDetailActivity.EXTRA_DOCTOR_SPECIALTY, DoctorAdapter.getPrimarySpecialty(doctor));
            if (doctor.getSpecializationTags() != null) {
                intent.putStringArrayListExtra(
                        DoctorDetailActivity.EXTRA_DOCTOR_SPECIALTIES,
                        new ArrayList<>(doctor.getSpecializationTags())
                );
            }
            intent.putExtra(DoctorDetailActivity.EXTRA_DOCTOR_ABOUT, doctor.getAbout());
            intent.putExtra(DoctorDetailActivity.EXTRA_DOCTOR_EXPERIENCE, doctor.getExperienceYears());
            intent.putExtra(DoctorDetailActivity.EXTRA_DOCTOR_RATING, doctor.getRating());
            intent.putExtra(DoctorDetailActivity.EXTRA_DOCTOR_REVIEW_COUNT, doctor.getReviewCount());
            intent.putExtra(DoctorDetailActivity.EXTRA_DOCTOR_FEE, doctor.getConsultationFeeUsd());
            startActivity(intent);
        });
        rv.setAdapter(adapter);

        EditText etSearch = view.findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Call the filter method in the adapter
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadDoctorsFromFirebase();

        return view;
    }

    private void loadDoctorsFromFirebase() {
        FirebaseDatabase.getInstance().getReference("doctors")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Doctor> firebaseDoctors = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Doctor doctor = child.getValue(Doctor.class);
                            if (doctor != null) {
                                if (doctor.getUid() == null || doctor.getUid().trim().isEmpty()) {
                                    doctor.setUid(child.getKey());
                                }
                                firebaseDoctors.add(doctor);
                            }
                        }
                        adapter.replaceData(firebaseDoctors);
                        emptyState.setVisibility(firebaseDoctors.isEmpty() ? View.VISIBLE : View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        emptyState.setText("Unable to load doctors");
                        emptyState.setVisibility(View.VISIBLE);
                    }
                });
    }
}
