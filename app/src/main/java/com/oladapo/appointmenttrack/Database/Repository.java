package com.oladapo.appointmenttrack.Database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

class Repository {

    private DAO dao;
    private LiveData<List<Appointments>>allAppointments;
    private LiveData<List<Appointments>>anyAppointment;

    Repository(Application application) {
        AppointmentsDatabase database = AppointmentsDatabase.getInstance(application);
        dao = database.appointmentsDao();
        allAppointments = dao.getAllAppointments();
        anyAppointment = dao.getAnyAppointment();
    }

    void insert(Appointments appointments) {
        new InsertAppointmentsAsyncTask(dao).execute(appointments);
    }

    void update(Appointments appointments) {
        new UpdateAppointmentsAsyncTask(dao).execute(appointments);
    }

    void delete(Appointments appointments) {
        new DeleteAppointmentsAsyncTask(dao).execute(appointments);
    }

    void deleteAllAppointments() {
        new DeleteAllAppointmentsAsyncTask(dao).execute();
    }

    LiveData<List<Appointments>> getAllAppointments() {
        return allAppointments;
    }

    LiveData<List<Appointments>> getAnyAppointment() {
        return anyAppointment;
    }

    private static class InsertAppointmentsAsyncTask extends AsyncTask<Appointments, Void, Void> {
        private DAO dao;
        private InsertAppointmentsAsyncTask(DAO dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Appointments... appointments) {
            dao.insert(appointments[0]);
            return null;
        }
    }

    private static class UpdateAppointmentsAsyncTask extends AsyncTask<Appointments, Void, Void> {
        private DAO dao;

        private UpdateAppointmentsAsyncTask(DAO dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Appointments... appointments) {
            dao.update(appointments[0]);
            return null;
        }
    }

    private static class DeleteAppointmentsAsyncTask extends AsyncTask<Appointments, Void, Void> {
        private DAO dao;

        private DeleteAppointmentsAsyncTask(DAO dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Appointments... appointments) {
            dao.delete(appointments[0]);
            return null;
        }
    }

    private static class DeleteAllAppointmentsAsyncTask extends AsyncTask<Void, Void, Void> {
        private DAO dao;

        private DeleteAllAppointmentsAsyncTask(DAO dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            dao.deleteAllAppointments();
            return null;
        }
    }
}
