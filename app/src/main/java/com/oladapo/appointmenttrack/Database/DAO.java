package com.oladapo.appointmenttrack.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DAO {

    @Insert
    void insert(Appointments appointments);

    @Update
    void update(Appointments appointments);

    @Delete
    void delete(Appointments appointments);

    @Query("DELETE FROM appointments_table")
    void deleteAllAppointments();

    @Query("SELECT * FROM appointments_table ORDER BY date('%02d %M %y') ASC, time('%02d:%02d')ASC")
    LiveData<List<Appointments>> getAllAppointments();
}
