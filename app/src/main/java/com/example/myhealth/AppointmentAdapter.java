package com.example.myhealth;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    public interface OnMarkDoneClickListener {
        void onMarkDone(Appointment appointment);
    }

    private final List<Appointment> appointmentList;
    private final OnMarkDoneClickListener markDoneClickListener;

    public AppointmentAdapter(List<Appointment> appointmentList) {
        this(appointmentList, null);
    }

    public AppointmentAdapter(List<Appointment> appointmentList, OnMarkDoneClickListener markDoneClickListener) {
        this.appointmentList = appointmentList;
        this.markDoneClickListener = markDoneClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appt = appointmentList.get(position);
        holder.name.setText(safeText(appt.getPatientName(), "Patient"));
        holder.time.setText(safeText(appt.getTime(), "--"));
        holder.date.setText(safeText(appt.getDate(), "Date not set"));
        holder.status.setText(safeText(appt.getStatus(), "Pending"));

        boolean isDone = "Done".equalsIgnoreCase(appt.getStatus())
                || "Completed".equalsIgnoreCase(appt.getStatus());
        holder.markDoneButton.setVisibility(markDoneClickListener == null ? View.GONE : View.VISIBLE);
        holder.markDoneButton.setEnabled(!isDone);
        holder.markDoneButton.setText(isDone ? "Done" : "Mark Done");
        holder.markDoneButton.setOnClickListener(v -> {
            if (markDoneClickListener != null) {
                markDoneClickListener.onMarkDone(appt);
            }
        });
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, time, date, status;
        MaterialButton markDoneButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvPatientName);
            time = itemView.findViewById(R.id.tvApptTime);
            date = itemView.findViewById(R.id.tvApptDate);
            status = itemView.findViewById(R.id.tvApptStatus);
            markDoneButton = itemView.findViewById(R.id.btnMarkDone);
        }
    }

    private String safeText(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }
}
