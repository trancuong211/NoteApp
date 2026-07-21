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
import com.example.noteapp.adapter.TaskAdapter;
import com.example.noteapp.model.Task;
import com.example.noteapp.viewmodel.TaskViewModel;
import java.util.List;

public class TasksFragment extends Fragment {

    private TaskViewModel taskViewModel;
    private TaskAdapter taskAdapter;
    private TextView tvEmptyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);

        tvEmptyState = view.findViewById(R.id.tv_empty_state);
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
        });

        // Add button in header
        TextView btnAdd = view.findViewById(R.id.btn_add);
        if (btnAdd != null) {
            btnAdd.setOnClickListener(v -> {
                NewTaskDialogFragment dialog = new NewTaskDialogFragment();
                dialog.show(getParentFragmentManager(), "NewTaskDialog");
            });
        }
    }
}
