package com.example.noteapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.noteapp.model.Note;
import java.util.ArrayList;
import java.util.List;

public class NoteViewModel extends ViewModel {
    private final MutableLiveData<List<Note>> notes = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<Note>> getNotes() {
        return notes;
    }

    public void addNote(Note note) {
        List<Note> currentNotes = notes.getValue();
        if (currentNotes != null) {
            currentNotes.add(0, note);
            notes.setValue(currentNotes);
        }
    }

    public void removeNote(int position) {
        List<Note> currentNotes = notes.getValue();
        if (currentNotes != null && position >= 0 && position < currentNotes.size()) {
            currentNotes.remove(position);
            notes.setValue(currentNotes);
        }
    }

    public int getNoteCount() {
        List<Note> currentNotes = notes.getValue();
        return currentNotes != null ? currentNotes.size() : 0;
    }
}
