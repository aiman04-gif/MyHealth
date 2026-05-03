package com.example.myhealth;

public class Appointment {
    private String appointmentId;
    private String doctorUid;
    private String doctorName;
    private String patientUid;
    private String patientName;
    private String date;
    private String time;
    private String status;
    private long createdAt;

    public Appointment() {}

    public Appointment(String appointmentId, String doctorUid, String doctorName,
                       String patientUid, String patientName, String date,
                       String time, String status, long createdAt) {
        this.appointmentId = appointmentId;
        this.doctorUid = doctorUid;
        this.doctorName = doctorName;
        this.patientUid = patientUid;
        this.patientName = patientName;
        this.date = date;
        this.time = time;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getDoctorUid() {
        return doctorUid;
    }

    public void setDoctorUid(String doctorUid) {
        this.doctorUid = doctorUid;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getPatientUid() {
        return patientUid;
    }

    public void setPatientUid(String patientUid) {
        this.patientUid = patientUid;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
