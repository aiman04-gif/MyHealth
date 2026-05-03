package com.example.myhealth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView avatarText = view.findViewById(R.id.text_profile_avatar);
        TextView nameText = view.findViewById(R.id.text_profile_name);
        TextView emailText = view.findViewById(R.id.text_profile_email);
        MaterialButton logoutButton = view.findViewById(R.id.button_logout);

        SharedPreferences sp = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String name = sp.getString("user_name", getString(R.string.profile_user_name));
        String email = sp.getString("user_email", getString(R.string.profile_user_email));

        avatarText.setText(getInitials(name));
        nameText.setText(name);
        emailText.setText(email);
        logoutButton.setOnClickListener(v -> showLogoutDialog());

        return view;
    }

    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getString(R.string.profile_avatar_initials);
        }

        String[] parts = name.trim().split("\\s+");
        String first = parts[0].substring(0, 1);
        String second = parts.length > 1 ? parts[1].substring(0, 1) : "";
        return (first + second).toUpperCase();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.logout_confirm_title)
                .setMessage(R.string.logout_confirm_message)
                .setPositiveButton(R.string.logout, (dialog, which) -> logout())
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        requireActivity()
                .getSharedPreferences("user_session", Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();

        Intent intent = new Intent(requireContext(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
