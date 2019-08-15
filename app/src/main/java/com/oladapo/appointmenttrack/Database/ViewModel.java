package com.oladapo.appointmenttrack.Database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ViewModel extends AndroidViewModel {

    private Repository repository;
    private LiveData<List<Appointments>> allAppointments;
    private LiveData<List<Appointments>>anyAppointment;

    public ViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
        allAppointments = repository.getAllAppointments();
        anyAppointment = repository.getAnyAppointment();
    }

    public void insert(Appointments appointments) {
        repository.insert(appointments);
    }

    public void update(Appointments appointments) {
        repository.update(appointments);
    }

    public void delete(Appointments appointments) {
        repository.delete(appointments);
    }

    public void deleteAllAppointments() {
        repository.deleteAllAppointments();
    }

    public LiveData<List<Appointments>> getAllAppointments() {
        return allAppointments;
    }

    public LiveData<List<Appointments>> getAnyAppointment() {
        return anyAppointment;
    }
}
