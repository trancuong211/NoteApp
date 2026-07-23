package com.example.noteapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.noteapp.model.Note;
import java.util.List;

@Dao
public interface NoteDao {
    @Query("SELECT * FROM notes WHERE userId = :userId ORDER BY id DESC")
    LiveData<List<Note>> getAll(int userId);

    @Query("SELECT * FROM notes WHERE userId = :userId ORDER BY id DESC")
    List<Note> getAllSync(int userId);

    @Insert
    void insert(Note note);

    @Delete
    void delete(Note note);

    @Query("DELETE FROM notes WHERE id = :id")
    void deleteById(int id);

    @Query("SELECT COUNT(*) FROM notes WHERE userId = :userId")
    LiveData<Integer> getCount(int userId);
}
