package com.oladapo.appointmenttrack.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Appointments.class}, version = 1, exportSchema = false)
abstract class AppointmentsDatabase extends RoomDatabase {

    private static AppointmentsDatabase instance;

    abstract DAO appointmentsDao();

    static synchronized AppointmentsDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppointmentsDatabase.class, "appointments_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
