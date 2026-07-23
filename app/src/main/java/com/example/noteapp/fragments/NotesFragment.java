package com.example.noteapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.noteapp.R;
import com.example.noteapp.adapter.NoteAdapter;
import com.example.noteapp.model.Note;
import com.example.noteapp.util.UserManager;
import com.example.noteapp.viewmodel.NoteViewModel;
import java.util.List;

public class NotesFragment extends Fragment {

    private NoteViewModel noteViewModel;
    private NoteAdapter noteAdapter;
    private TextView tvEmptyState;
    private TextView tvNotesCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        noteViewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);
        noteViewModel.setUserId(UserManager.getUserId(requireContext()));

        tvEmptyState = view.findViewById(R.id.tv_empty_state);
        tvNotesCount = view.findViewById(R.id.tv_notes_count);
        RecyclerView rvNotes = view.findViewById(R.id.rv_notes);
        rvNotes.setLayoutManager(new LinearLayoutManager(getContext()));

        noteAdapter = new NoteAdapter(new NoteAdapter.OnNoteClickListener() {
            @Override
            public void onNoteClick(Note note) {
                NoteDetailDialogFragment dialog = NoteDetailDialogFragment.newInstance(
                        note.getTitle(), note.getContent(), note.getCategory());
                dialog.show(getParentFragmentManager(), "NoteDetailDialog");
            }

            @Override
            public void onDeleteClick(Note note) {
                noteViewModel.delete(note);
            }
        });
        rvNotes.setAdapter(noteAdapter);

        noteViewModel.getNotes().observe(getViewLifecycleOwner(), notes -> {
            if (notes == null || notes.isEmpty()) {
                tvEmptyState.setVisibility(View.VISIBLE);
                rvNotes.setVisibility(View.GONE);
                tvNotesCount.setText("0 notes");
            } else {
                tvEmptyState.setVisibility(View.GONE);
                rvNotes.setVisibility(View.VISIBLE);
                noteAdapter.setNotes(notes);
                tvNotesCount.setText(notes.size() + " notes");
            }
        });

        // Add button in header
        TextView btnAdd = view.findViewById(R.id.btn_add);
        if (btnAdd != null) {
            btnAdd.setOnClickListener(v -> {
                NewNoteDialogFragment dialog = new NewNoteDialogFragment();
                dialog.show(getParentFragmentManager(), "NewNoteDialog");
            });
        }
    }
}
