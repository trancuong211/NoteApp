package com.example.noteapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.noteapp.model.Reminder;
import java.util.List;

@Dao
public interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE userId = :userId ORDER BY id DESC")
    LiveData<List<Reminder>> getAll(int userId);

    @Query("SELECT * FROM reminders WHERE userId = :userId ORDER BY id DESC")
    List<Reminder> getAllSync(int userId);

    @Query("SELECT * FROM reminders WHERE userId = :userId AND active = 1 ORDER BY id DESC")
    LiveData<List<Reminder>> getActive(int userId);

    @Query("SELECT * FROM reminders WHERE userId = :userId AND active = 0 ORDER BY id DESC")
    LiveData<List<Reminder>> getInactive(int userId);

    @Insert
    long insert(Reminder reminder);

    @Update
    void update(Reminder reminder);

    @Delete
    void delete(Reminder reminder);

    @Query("DELETE FROM reminders WHERE id = :id")
    void deleteById(int id);

    @Query("SELECT COUNT(*) FROM reminders WHERE userId = :userId")
    LiveData<Integer> getCount(int userId);

    @Query("SELECT COUNT(*) FROM reminders WHERE userId = :userId AND active = 1")
    LiveData<Integer> getActiveCount(int userId);

    @Query("SELECT COUNT(*) FROM reminders WHERE userId = :userId AND active = 0")
    LiveData<Integer> getInactiveCount(int userId);
}
