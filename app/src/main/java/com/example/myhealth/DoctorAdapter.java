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

    private List<Doctor> doctorList;
    private List<Doctor> doctorListFull;
    public DoctorAdapter(List<Doctor> doctorList)
    {
        this.doctorList=doctorList;
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

        holder.tvName.setText(doc.getName());
        holder.tvSpeciality.setText(doc.getSpecializationTags().get(0));
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
                // Filter by name or specialty
                if (item.getName().toLowerCase().contains(text) ||
                        item.getSpecializationTags().get(0).toLowerCase().contains(text)) {
                    doctorList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class DoctorViewHolder extends RecyclerView.ViewHolder{
            TextView tvName, tvSpeciality;

            public DoctorViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName=itemView.findViewById(R.id.tvDoctorName);
                tvSpeciality=itemView.findViewById(R.id.tvSpecialty);
            }
        }
}
