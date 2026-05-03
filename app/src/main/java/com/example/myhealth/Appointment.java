package com.example.myhealth;

public class Appointment {
    private String appointmentId;
    private String patientName;
    private String patientUid;
    private String time;
    private String date;
    private String status; // e.g., "Pending", "Confirmed", "Completed"

    public Appointment() {} // Required for Firebase

    public Appointment(String appointmentId, String patientName, String time, String date, String status) {
        this.appointmentId = appointmentId;
        this.patientName = patientName;
        this.time = time;
        this.date = date;
        this.status = status;
    }

    // Getters and Setters
    public String getPatientName() { return patientName; }
    public String getTime() { return time; }
    public String getDate() { return date; }
    public String getStatus() { return status; }
}
