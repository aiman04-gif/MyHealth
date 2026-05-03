package com.example.myhealth;

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

import java.util.ArrayList;
import java.util.List;


public class DoctorFragment extends Fragment {



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

        List<String> specializationTags=new ArrayList<>();
        specializationTags.add("Pulmonologist");

        // Create some dummy data
        List<Doctor> doctors = new ArrayList<>();
        doctors.add(new Doctor("Dr Sara Ali Khan",4,4.5,5,specializationTags,"xyz",300));
        doctors.add(new Doctor("Dr Maheen",2,3.5,5,specializationTags,"xyz",250));
        doctors.add(new Doctor("Dr Asad",1,2,5,specializationTags,"xyz",100));

        // Set the adapter
        DoctorAdapter adapter = new DoctorAdapter(doctors);
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

        return view;
    }
}