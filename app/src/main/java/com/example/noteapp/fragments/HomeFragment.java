package com.example.noteapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.noteapp.R;
import com.example.noteapp.adapter.NoteAdapter;
import com.example.noteapp.adapter.TaskAdapter;
import com.example.noteapp.model.Note;
import com.example.noteapp.model.Task;
import com.example.noteapp.viewmodel.NoteViewModel;
import com.example.noteapp.viewmodel.TaskViewModel;
import java.util.List;

public class HomeFragment extends Fragment {

    private TaskViewModel taskViewModel;
    private NoteViewModel noteViewModel;
    private TaskAdapter taskAdapter;
    private NoteAdapter noteAdapter;
    private TextView tvEmptyState;
    private TextView tvEmptyStateNotes;
    private TextView tvProgressPercent;
    private TextView tvTasksSummary;
    private TextView tvPendingCount;
    private TextView tvDoneCount;
    private TextView tvNotesCount;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
        noteViewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);

        tvEmptyState = view.findViewById(R.id.tv_empty_state);
        tvEmptyStateNotes = view.findViewById(R.id.tv_empty_state_notes);
        tvProgressPercent = view.findViewById(R.id.tv_progress_percent);
        tvTasksSummary = view.findViewById(R.id.tv_tasks_summary);
        tvPendingCount = view.findViewById(R.id.tv_pending_count);
        tvDoneCount = view.findViewById(R.id.tv_done_count);
        tvNotesCount = view.findViewById(R.id.tv_notes_count);
        progressBar = view.findViewById(R.id.progress_bar);

        // Setup Tasks RecyclerView
        RecyclerView rvTasks = view.findViewById(R.id.rv_tasks);
        rvTasks.setLayoutManager(new LinearLayoutManager(getContext()));

        taskAdapter = new TaskAdapter(new TaskAdapter.OnTaskClickListener() {
            @Override
            public void onTaskClick(int position) {
                List<Task> tasks = taskViewModel.getTasks().getValue();
                if (tasks != null && position >= 0 && position < tasks.size()) {
                    Task task = tasks.get(position);
                    TaskDetailDialogFragment dialog = TaskDetailDialogFragment.newInstance(task);
                    dialog.show(getParentFragmentManager(), "TaskDetailDialog");
                }
            }

            @Override
            public void onTaskDoneChanged(int position, boolean isDone) {
                taskViewModel.toggleTaskDone(position);
            }

            @Override
            public void onDeleteClick(int position) {
                taskViewModel.removeTask(position);
            }
        });
        rvTasks.setAdapter(taskAdapter);

        taskViewModel.getTasks().observe(getViewLifecycleOwner(), tasks -> {
            if (tasks == null || tasks.isEmpty()) {
                tvEmptyState.setVisibility(View.VISIBLE);
                rvTasks.setVisibility(View.GONE);
            } else {
                tvEmptyState.setVisibility(View.GONE);
                rvTasks.setVisibility(View.VISIBLE);
                taskAdapter.setTasks(tasks);
            }
            updateProgress();
        });

        // Setup Notes RecyclerView
        RecyclerView rvNotes = view.findViewById(R.id.rv_notes);
        rvNotes.setLayoutManager(new LinearLayoutManager(getContext()));

        noteAdapter = new NoteAdapter(new NoteAdapter.OnNoteClickListener() {
            @Override
            public void onNoteClick(int position) {
                List<Note> notes = noteViewModel.getNotes().getValue();
                if (notes != null && position >= 0 && position < notes.size()) {
                    Note note = notes.get(position);
                    NoteDetailDialogFragment dialog = NoteDetailDialogFragment.newInstance(
                            note.getTitle(), note.getContent(), note.getCategory());
                    dialog.show(getParentFragmentManager(), "NoteDetailDialog");
                }
            }

            @Override
            public void onDeleteClick(int position) {
                noteViewModel.removeNote(position);
            }
        });
        rvNotes.setAdapter(noteAdapter);

        noteViewModel.getNotes().observe(getViewLifecycleOwner(), notes -> {
            if (notes == null || notes.isEmpty()) {
                tvEmptyStateNotes.setVisibility(View.VISIBLE);
                rvNotes.setVisibility(View.GONE);
            } else {
                tvEmptyStateNotes.setVisibility(View.GONE);
                rvNotes.setVisibility(View.VISIBLE);
                noteAdapter.setNotes(notes);
            }
            updateNotesCount();
        });

        // New Task button
        View btnNewTask = view.findViewById(R.id.btn_new_task);
        btnNewTask.setOnClickListener(v -> {
            NewTaskDialogFragment dialog = new NewTaskDialogFragment();
            dialog.show(getParentFragmentManager(), "NewTaskDialog");
        });

        // New Note button
        View btnNewNote = view.findViewById(R.id.btn_new_note);
        btnNewNote.setOnClickListener(v -> {
            NewNoteDialogFragment dialog = new NewNoteDialogFragment();
            dialog.show(getParentFragmentManager(), "NewNoteDialog");
        });
    }

    private void updateProgress() {
        int total = taskViewModel.getTasks().getValue() != null ? taskViewModel.getTasks().getValue().size() : 0;
        int done = taskViewModel.getDoneCount();
        int pending = taskViewModel.getPendingCount();

        tvTasksSummary.setText(done + " of " + total + " tasks done");
        tvPendingCount.setText(pending + " Pending");
        tvDoneCount.setText(done + " Done");

        int percent = total > 0 ? (done * 100 / total) : 0;
        tvProgressPercent.setText(percent + "%");
        if (progressBar != null) {
            progressBar.setProgress(percent);
        }
    }

    private void updateNotesCount() {
        int noteCount = noteViewModel.getNoteCount();
        tvNotesCount.setText(noteCount + " Notes");
    }
}
