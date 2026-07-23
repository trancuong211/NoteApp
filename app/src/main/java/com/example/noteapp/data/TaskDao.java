package com.example.noteapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.noteapp.model.Task;
import java.util.List;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY id DESC")
    LiveData<List<Task>> getAll(int userId);

    @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY id DESC")
    List<Task> getAllSync(int userId);

    @Query("SELECT * FROM tasks WHERE userId = :userId AND dateKey = :dateKey ORDER BY id DESC")
    LiveData<List<Task>> getByDate(int userId, String dateKey);

    @Query("SELECT * FROM tasks WHERE userId = :userId AND dateKey = :dateKey")
    List<Task> getByDateSync(int userId, String dateKey);

    @Query("SELECT * FROM tasks WHERE userId = :userId AND status = :status ORDER BY id DESC")
    LiveData<List<Task>> getByStatus(int userId, String status);

    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("DELETE FROM tasks WHERE id = :id")
    void deleteById(int id);

    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId")
    LiveData<Integer> getCount(int userId);

    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId AND status = 'done'")
    LiveData<Integer> getDoneCount(int userId);

    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId AND status != 'done'")
    LiveData<Integer> getPendingCount(int userId);

    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId AND status = 'inprogress'")
    LiveData<Integer> getInProgressCount(int userId);
}
