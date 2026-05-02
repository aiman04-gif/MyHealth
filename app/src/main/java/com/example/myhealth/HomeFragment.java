package com.example.myhealth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


public class HomeFragment extends Fragment {

    private TextView tvName;


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

        tvName=v.findViewById(R.id.userName);
        SharedPreferences sp = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String email = sp.getString("user_email", null);
        String name = sp.getString("user_name", "");

        if (!name.isEmpty()) {
            tvName.setText("Hi "+name);
        }

        // Inflate the layout for this fragment
        return v;
    }
}