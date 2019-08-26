package com.oladapo.appointmenttrack.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "appointments_table")
public class Appointments {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String clientName;
    private String clientPhone;
    private String clientEmail;
    private String description;
    private String date;
    private String time;
    private String dateTime;
    private String clientReminderDate;
    private String clientReminderTime;
    private String clientReminderMessage;
    private String dateAdded;

    private int reminderTime;
    private int reminderState;
    private int clientReminderState;

    private boolean sms;
    private boolean email;
    private boolean both;

    public Appointments(String clientName, String clientPhone, String clientEmail,
                        String description, String date, String time, int reminderTime, int reminderState, int clientReminderState,
                        String clientReminderDate, String  clientReminderTime, String clientReminderMessage,
                        String dateAdded, boolean sms, boolean email, boolean both, String dateTime) {
        this.clientName = clientName;
        this.clientPhone = clientPhone;
        this.clientEmail = clientEmail;
        this.description = description;
        this.date = date;
        this.time = time;
        this.reminderTime = reminderTime;
        this.reminderState = reminderState;
        this.clientReminderState = clientReminderState;
        this.clientReminderDate = clientReminderDate;
        this.clientReminderTime = clientReminderTime;
        this.clientReminderMessage = clientReminderMessage;
        this.dateAdded = dateAdded;
        this.sms = sms;
        this.email = email;
        this.both = both;
        this.dateTime = dateTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getReminderTime() {
        return reminderTime;
    }

    public int getReminderState() {
        return reminderState;
    }

    public int getClientReminderState() {
        return clientReminderState;
    }

    public String getClientReminderTime() {
        return clientReminderTime;
    }

    public String getClientReminderDate() {
        return clientReminderDate;
    }

    public String getClientReminderMessage() {
        return clientReminderMessage;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public boolean isSms() {
        return sms;
    }

    public boolean isEmail() {
        return email;
    }

    public boolean isBoth() {
        return both;
    }

    public String getDateTime() {
        return dateTime;
    }
}