package com.example.noteapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.noteapp.R;
import com.example.noteapp.model.Task;
import com.example.noteapp.viewmodel.TaskViewModel;

public class NewTaskDialogFragment extends DialogFragment {

    private String selectedCategory = "Work";
    private String selectedPriority = "Medium";
    private TaskViewModel taskViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_new_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);

        ImageView btnClose = view.findViewById(R.id.btn_close);
        EditText etTaskTitle = view.findViewById(R.id.et_task_title);
        TextView btnAddTask = view.findViewById(R.id.btn_add_task);

        // Category chips
        TextView chipWork = view.findViewById(R.id.chip_work);
        TextView chipPersonal = view.findViewById(R.id.chip_personal);
        TextView chipStudy = view.findViewById(R.id.chip_study);
        TextView chipHealth = view.findViewById(R.id.chip_health);

        // Priority chips
        TextView chipHigh = view.findViewById(R.id.chip_high);
        TextView chipMedium = view.findViewById(R.id.chip_medium);
        TextView chipLow = view.findViewById(R.id.chip_low);

        // Close button
        btnClose.setOnClickListener(v -> dismiss());

        // Category click listeners
        View.OnClickListener categoryClickListener = v -> {
            resetCategoryChips(chipWork, chipPersonal, chipStudy, chipHealth);
            int id = v.getId();
            if (id == R.id.chip_work) {
                selectedCategory = "Work";
                chipWork.setBackgroundResource(R.drawable.bg_chip_category_selected);
                chipWork.setTextColor(getResources().getColor(R.color.tag_work_text));
            } else if (id == R.id.chip_personal) {
                selectedCategory = "Personal";
                chipPersonal.setBackgroundResource(R.drawable.bg_chip_category_selected);
                chipPersonal.setTextColor(getResources().getColor(R.color.tag_personal_text));
            } else if (id == R.id.chip_study) {
                selectedCategory = "Study";
                chipStudy.setBackgroundResource(R.drawable.bg_chip_category_selected);
                chipStudy.setTextColor(getResources().getColor(R.color.tag_study_text));
            } else if (id == R.id.chip_health) {
                selectedCategory = "Health";
                chipHealth.setBackgroundResource(R.drawable.bg_chip_category_selected);
                chipHealth.setTextColor(getResources().getColor(R.color.tag_health_text));
            }
        };

        chipWork.setOnClickListener(categoryClickListener);
        chipPersonal.setOnClickListener(categoryClickListener);
        chipStudy.setOnClickListener(categoryClickListener);
        chipHealth.setOnClickListener(categoryClickListener);

        // Priority click listeners
        View.OnClickListener priorityClickListener = v -> {
            resetPriorityChips(chipHigh, chipMedium, chipLow);
            int id = v.getId();
            if (id == R.id.chip_high) {
                selectedPriority = "High";
                chipHigh.setBackgroundResource(R.drawable.bg_chip_priority_selected_high);
                chipHigh.setTextColor(getResources().getColor(R.color.tag_high_text));
            } else if (id == R.id.chip_medium) {
                selectedPriority = "Medium";
                chipMedium.setBackgroundResource(R.drawable.bg_chip_priority_selected_medium);
                chipMedium.setTextColor(getResources().getColor(R.color.tag_medium_text));
            } else if (id == R.id.chip_low) {
                selectedPriority = "Low";
                chipLow.setBackgroundResource(R.drawable.bg_chip_priority_selected_low);
                chipLow.setTextColor(getResources().getColor(R.color.tag_low_text));
            }
        };

        chipHigh.setOnClickListener(priorityClickListener);
        chipMedium.setOnClickListener(priorityClickListener);
        chipLow.setOnClickListener(priorityClickListener);

        // Add Task button
        btnAddTask.setOnClickListener(v -> {
            String title = etTaskTitle.getText().toString().trim();
            if (title.isEmpty()) {
                etTaskTitle.setError("Please enter a task title");
                return;
            }
            Task newTask = new Task(title, selectedCategory, selectedPriority);
            taskViewModel.addTask(newTask);
            dismiss();
        });
    }

    private void resetCategoryChips(TextView... chips) {
        for (TextView chip : chips) {
            chip.setBackgroundResource(R.drawable.bg_chip_category_default);
            chip.setTextColor(getResources().getColor(R.color.text_secondary));
        }
    }

    private void resetPriorityChips(TextView... chips) {
        for (TextView chip : chips) {
            chip.setBackgroundResource(R.drawable.bg_chip_priority_default);
            chip.setTextColor(getResources().getColor(R.color.text_secondary));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setGravity(android.view.Gravity.BOTTOM);
        }
    }
}
