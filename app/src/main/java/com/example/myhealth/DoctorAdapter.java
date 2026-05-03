package com.example.myhealth;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    public interface OnDoctorClickListener {
        void onDoctorClick(Doctor doctor);
    }

    private final List<Doctor> doctorList;
    private final List<Doctor> doctorListFull;
    private final OnDoctorClickListener listener;

    public DoctorAdapter(List<Doctor> doctorList, OnDoctorClickListener listener)
    {
        this.doctorList=doctorList;
        this.listener = listener;
        doctorListFull= new ArrayList<>(doctorList);
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_doctor, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doc=doctorList.get(position);

        holder.tvName.setText(safeText(doc.getName(), "Doctor"));
        holder.tvSpeciality.setText(getPrimarySpecialty(doc));
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDoctorClick(doc);
            }
        });
        holder.btnGo.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDoctorClick(doc);
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public void filter(String text) {
        doctorList.clear();
        if (text.isEmpty()) {
            doctorList.addAll(doctorListFull);
        } else {
            text = text.toLowerCase();
            for (Doctor item : doctorListFull) {
                String name = safeText(item.getName(), "").toLowerCase();
                String specialty = getPrimarySpecialty(item).toLowerCase();
                if (name.contains(text) || specialty.contains(text)) {
                    doctorList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void replaceData(List<Doctor> doctors) {
        doctorList.clear();
        doctorList.addAll(doctors);
        doctorListFull.clear();
        doctorListFull.addAll(doctors);
        notifyDataSetChanged();
    }

    public static String getPrimarySpecialty(Doctor doctor) {
        if (doctor.getSpecializationTags() == null || doctor.getSpecializationTags().isEmpty()) {
            return "General Physician";
        }
        return safeText(doctor.getSpecializationTags().get(0), "General Physician");
    }

    private static String safeText(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }

    public static class DoctorViewHolder extends RecyclerView.ViewHolder{
            TextView tvName, tvSpeciality;
            View btnGo;

            public DoctorViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName=itemView.findViewById(R.id.tvDoctorName);
                tvSpeciality=itemView.findViewById(R.id.tvSpecialty);
                btnGo=itemView.findViewById(R.id.btnGo);
            }
        }
}
