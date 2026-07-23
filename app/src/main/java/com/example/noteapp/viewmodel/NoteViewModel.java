package com.example.noteapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.noteapp.data.AppDatabase;
import com.example.noteapp.data.NoteDao;
import com.example.noteapp.model.Note;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NoteViewModel extends AndroidViewModel {

    private final NoteDao noteDao;
    private final ExecutorService executor;
    private int userId;
    private LiveData<List<Note>> allNotes;
    private LiveData<Integer> noteCount;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        noteDao = db.noteDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public void setUserId(int userId) {
        this.userId = userId;
        allNotes = noteDao.getAll(userId);
        noteCount = noteDao.getCount(userId);
    }

    public LiveData<List<Note>> getNotes() { return allNotes; }
    public LiveData<Integer> getNoteCount() { return noteCount; }

    public void insert(Note note) {
        executor.execute(() -> noteDao.insert(note));
    }

    public void delete(Note note) {
        executor.execute(() -> noteDao.delete(note));
    }

    public void deleteById(int id) {
        executor.execute(() -> noteDao.deleteById(id));
    }
}
