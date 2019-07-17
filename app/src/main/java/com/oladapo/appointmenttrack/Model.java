package com.oladapo.appointmenttrack;

public class Model {

    String clientName;
    String clientPhone;
    String description;
    String date;
    String time;
    String reminderDate;
    String dateAdded;

    public Model() {
    }

    public Model(String clientName, String clientPhone, String description, String date, String time, String reminderDate, String dateAdded) {
        this.clientName = clientName;
        this.clientPhone = clientPhone;
        this.description = description;
        this.date = date;
        this.time = time;
        this.reminderDate = reminderDate;
        this.dateAdded = dateAdded;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(String reminderDate) {
        this.reminderDate = reminderDate;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }
}
