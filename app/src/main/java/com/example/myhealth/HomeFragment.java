package com.example.myhealth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.Navigation;


public class HomeFragment extends Fragment {

    private TextView tvName;
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
        emergencyCall=v.findViewById(R.id.emergencyCall);
        nearbyHospitals=v.findViewById(R.id.nearbyHospitals);
        nearbyPharmacy=v.findViewById(R.id.nearbyPharmacy);
        bookAppointment=v.findViewById(R.id.bookAppointment);
        upcomingConsultation=v.findViewById(R.id.upcomingConsultation);
        cardprofile=v.findViewById(R.id.cardProfile);

    }
}
