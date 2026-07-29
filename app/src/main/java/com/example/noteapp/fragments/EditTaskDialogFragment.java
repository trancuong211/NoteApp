package com.example.noteapp.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.noteapp.R;
import com.example.noteapp.model.Task;
import com.example.noteapp.viewmodel.TaskViewModel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditTaskDialogFragment extends DialogFragment {

    private static final String ARG_TASK_ID = "task_id";
    private static final String ARG_TITLE = "title";
    private static final String ARG_CATEGORY = "category";
    private static final String ARG_PRIORITY = "priority";
    private static final String ARG_STATUS = "status";
    private static final String ARG_START = "start";
    private static final String ARG_DEADLINE = "deadline";
    private static final String ARG_DATE_KEY = "dateKey";

    private String selectedCategory = "work";
    private String selectedPriority = "medium";
    private String selectedStatus = "todo";
    private TaskViewModel taskViewModel;
    private int taskId;

    private String deadlineDate = "";
    private String deadlineTime = "";
    private String startDate = "";
    private String startTime = "";

    public static EditTaskDialogFragment newInstance(Task task) {
        EditTaskDialogFragment fragment = new EditTaskDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TASK_ID, task.getId());
        args.putString(ARG_TITLE, task.getTitle());
        args.putString(ARG_CATEGORY, task.getCategory());
        args.putString(ARG_PRIORITY, task.getPriority());
        args.putString(ARG_STATUS, task.getStatus());
        args.putString(ARG_START, task.getStartTime());
        args.putString(ARG_DEADLINE, task.getDeadline());
        args.putString(ARG_DATE_KEY, task.getDateKey());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_edit_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);

        ImageView btnClose = view.findViewById(R.id.btn_close);
        EditText etTaskTitle = view.findViewById(R.id.et_task_title);
        TextView btnSaveTask = view.findViewById(R.id.btn_save_task);

        EditText etDeadlineDate = view.findViewById(R.id.et_deadline_date);
        EditText etDeadlineTime = view.findViewById(R.id.et_deadline_time);
        EditText etStartDate = view.findViewById(R.id.et_start_date);
        EditText etStartTime = view.findViewById(R.id.et_start_time);

        TextView chipWork = view.findViewById(R.id.chip_work);
        TextView chipPersonal = view.findViewById(R.id.chip_personal);
        TextView chipStudy = view.findViewById(R.id.chip_study);
        TextView chipHealth = view.findViewById(R.id.chip_health);
        TextView chipHigh = view.findViewById(R.id.chip_high);
        TextView chipMedium = view.findViewById(R.id.chip_medium);
        TextView chipLow = view.findViewById(R.id.chip_low);
        TextView chipStatusTodo = view.findViewById(R.id.chip_status_todo);
        TextView chipStatusInprogress = view.findViewById(R.id.chip_status_inprogress);
        TextView chipStatusDone = view.findViewById(R.id.chip_status_done);

        btnClose.setOnClickListener(v -> dismiss());

        etDeadlineDate.setOnClickListener(v -> showDatePicker(date -> {
            deadlineDate = date;
            etDeadlineDate.setText(date);
        }));

        etDeadlineTime.setOnClickListener(v -> showTimePicker(time -> {
            deadlineTime = time;
            etDeadlineTime.setText(time);
        }));

        etStartDate.setOnClickListener(v -> showDatePicker(date -> {
            startDate = date;
            etStartDate.setText(date);
        }));

        etStartTime.setOnClickListener(v -> showTimePicker(time -> {
            startTime = time;
            etStartTime.setText(time);
        }));

        if (getArguments() != null) {
            taskId = getArguments().getInt(ARG_TASK_ID, 0);
            String title = getArguments().getString(ARG_TITLE, "");
            selectedCategory = getArguments().getString(ARG_CATEGORY, "work");
            selectedPriority = getArguments().getString(ARG_PRIORITY, "medium");
            selectedStatus = getArguments().getString(ARG_STATUS, "todo");
            String start = getArguments().getString(ARG_START, "");
            String deadline = getArguments().getString(ARG_DEADLINE, "");

            etTaskTitle.setText(title);

            if (!deadline.isEmpty()) {
                String[] parts = deadline.split(" ");
                if (parts.length >= 1) {
                    deadlineDate = parts[0];
                    etDeadlineDate.setText(deadlineDate);
                }
                if (parts.length >= 2) {
                    deadlineTime = parts[1];
                    etDeadlineTime.setText(deadlineTime);
                }
            }

            if (!start.isEmpty()) {
                String[] parts = start.split(" ");
                if (parts.length >= 1) {
                    startDate = parts[0];
                    etStartDate.setText(startDate);
                }
                if (parts.length >= 2) {
                    startTime = parts[1];
                    etStartTime.setText(startTime);
                }
            }

            selectCategoryChip(chipWork, chipPersonal, chipStudy, chipHealth);
            selectPriorityChip(chipHigh, chipMedium, chipLow);
            selectStatusChip(chipStatusTodo, chipStatusInprogress, chipStatusDone);
        }

        View.OnClickListener categoryClickListener = v -> {
            resetCategoryChips(chipWork, chipPersonal, chipStudy, chipHealth);
            int id = v.getId();
            if (id == R.id.chip_work) {
                selectedCategory = "work";
                chipWork.setBackgroundResource(R.drawable.bg_chip_category_selected);
                chipWork.setTextColor(getResources().getColor(R.color.tag_work_text));
            } else if (id == R.id.chip_personal) {
                selectedCategory = "personal";
                chipPersonal.setBackgroundResource(R.drawable.bg_chip_category_selected);
                chipPersonal.setTextColor(getResources().getColor(R.color.tag_personal_text));
            } else if (id == R.id.chip_study) {
                selectedCategory = "study";
                chipStudy.setBackgroundResource(R.drawable.bg_chip_category_selected);
                chipStudy.setTextColor(getResources().getColor(R.color.tag_study_text));
            } else if (id == R.id.chip_health) {
                selectedCategory = "health";
                chipHealth.setBackgroundResource(R.drawable.bg_chip_category_selected);
                chipHealth.setTextColor(getResources().getColor(R.color.tag_health_text));
            }
        };

        chipWork.setOnClickListener(categoryClickListener);
        chipPersonal.setOnClickListener(categoryClickListener);
        chipStudy.setOnClickListener(categoryClickListener);
        chipHealth.setOnClickListener(categoryClickListener);

        View.OnClickListener priorityClickListener = v -> {
            resetPriorityChips(chipHigh, chipMedium, chipLow);
            int id = v.getId();
            if (id == R.id.chip_high) {
                selectedPriority = "high";
                chipHigh.setBackgroundResource(R.drawable.bg_chip_priority_selected_high);
                chipHigh.setTextColor(getResources().getColor(R.color.tag_high_text));
            } else if (id == R.id.chip_medium) {
                selectedPriority = "medium";
                chipMedium.setBackgroundResource(R.drawable.bg_chip_priority_selected_medium);
                chipMedium.setTextColor(getResources().getColor(R.color.tag_medium_text));
            } else if (id == R.id.chip_low) {
                selectedPriority = "low";
                chipLow.setBackgroundResource(R.drawable.bg_chip_priority_selected_low);
                chipLow.setTextColor(getResources().getColor(R.color.tag_low_text));
            }
        };

        chipHigh.setOnClickListener(priorityClickListener);
        chipMedium.setOnClickListener(priorityClickListener);
        chipLow.setOnClickListener(priorityClickListener);

        View.OnClickListener statusClickListener = v -> {
            resetStatusChips(chipStatusTodo, chipStatusInprogress, chipStatusDone);
            int id = v.getId();
            if (id == R.id.chip_status_todo) {
                selectedStatus = "todo";
                chipStatusTodo.setBackgroundResource(R.drawable.bg_chip_category_selected);
                chipStatusTodo.setTextColor(getResources().getColor(R.color.tag_work_text));
            } else if (id == R.id.chip_status_inprogress) {
                selectedStatus = "inprogress";
                chipStatusInprogress.setBackgroundResource(R.drawable.bg_chip_category_selected);
                chipStatusInprogress.setTextColor(getResources().getColor(R.color.tag_personal_text));
            } else if (id == R.id.chip_status_done) {
                selectedStatus = "done";
                chipStatusDone.setBackgroundResource(R.drawable.bg_chip_category_selected);
                chipStatusDone.setTextColor(getResources().getColor(R.color.tag_health_text));
            }
        };

        chipStatusTodo.setOnClickListener(statusClickListener);
        chipStatusInprogress.setOnClickListener(statusClickListener);
        chipStatusDone.setOnClickListener(statusClickListener);

        btnSaveTask.setOnClickListener(v -> {
            String title = etTaskTitle.getText().toString().trim();
            if (title.isEmpty()) {
                etTaskTitle.setError("Vui lòng nhập tên nhiệm vụ");
                return;
            }

            String deadline = "";
            if (!deadlineDate.isEmpty()) {
                deadline = deadlineDate;
                if (!deadlineTime.isEmpty()) {
                    deadline += " " + deadlineTime;
                }
            }

            String start = "";
            if (!startTime.isEmpty()) {
                if (!startDate.isEmpty()) {
                    start = startDate + " " + startTime;
                } else if (!deadlineDate.isEmpty()) {
                    start = deadlineDate + " " + startTime;
                }
            } else if (!startDate.isEmpty()) {
                start = startDate;
            }

            String dateKey = "";
            if (!deadlineDate.isEmpty()) {
                try {
                    String[] parts = deadlineDate.split("/");
                    if (parts.length == 3) {
                        dateKey = parts[2] + "-" + parts[1] + "-" + parts[0];
                    }
                } catch (Exception e) {
                    dateKey = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                }
            }

            Task updatedTask = new Task(title, selectedCategory, selectedPriority, selectedStatus, start, deadline, dateKey);
            updatedTask.setId(taskId);
            updatedTask.setUserId(com.example.noteapp.util.UserManager.getUserId(requireContext()));
            taskViewModel.update(updatedTask);

            dismiss();
        });
    }

    private void selectCategoryChip(TextView chipWork, TextView chipPersonal, TextView chipStudy, TextView chipHealth) {
        resetCategoryChips(chipWork, chipPersonal, chipStudy, chipHealth);
        switch (selectedCategory) {
            case "work":
                chipWork.setBackgroundResource(R.drawable.bg_chip_category_selected);
                chipWork.setTextColor(getResources().getColor(R.color.tag_work_text));
                break;
            case "personal":
                chipPersonal.setBackgroundResource(R.drawable.bg_chip_category_selected);
                chipPersonal.setTextColor(getResources().getColor(R.color.tag_personal_text));
                break;
            case "study":
                chipStudy.setBackgroundResource(R.drawable.bg_chip_category_selected);
                chipStudy.setTextColor(getResources().getColor(R.color.tag_study_text));
                break;
            case "health":
                chipHealth.setBackgroundResource(R.drawable.bg_chip_category_selected);
                chipHealth.setTextColor(getResources().getColor(R.color.tag_health_text));
                break;
        }
    }

    private void selectPriorityChip(TextView chipHigh, TextView chipMedium, TextView chipLow) {
        resetPriorityChips(chipHigh, chipMedium, chipLow);
        switch (selectedPriority) {
            case "high":
                chipHigh.setBackgroundResource(R.drawable.bg_chip_priority_selected_high);
                chipHigh.setTextColor(getResources().getColor(R.color.tag_high_text));
                break;
            case "medium":
                chipMedium.setBackgroundResource(R.drawable.bg_chip_priority_selected_medium);
                chipMedium.setTextColor(getResources().getColor(R.color.tag_medium_text));
                break;
            case "low":
                chipLow.setBackgroundResource(R.drawable.bg_chip_priority_selected_low);
                chipLow.setTextColor(getResources().getColor(R.color.tag_low_text));
                break;
        }
    }

    private void selectStatusChip(TextView chipStatusTodo, TextView chipStatusInprogress, TextView chipStatusDone) {
        resetStatusChips(chipStatusTodo, chipStatusInprogress, chipStatusDone);
        switch (selectedStatus) {
            case "todo":
                chipStatusTodo.setBackgroundResource(R.drawable.bg_chip_category_selected);
                chipStatusTodo.setTextColor(getResources().getColor(R.color.tag_work_text));
                break;
            case "inprogress":
                chipStatusInprogress.setBackgroundResource(R.drawable.bg_chip_category_selected);
                chipStatusInprogress.setTextColor(getResources().getColor(R.color.tag_personal_text));
                break;
            case "done":
                chipStatusDone.setBackgroundResource(R.drawable.bg_chip_category_selected);
                chipStatusDone.setTextColor(getResources().getColor(R.color.tag_health_text));
                break;
        }
    }

    private void showDatePicker(OnDateTimeSetListener listener) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (dateView, year, month, dayOfMonth) -> {
            String date = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
            listener.onDateTimeSet(date);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker(OnDateTimeSetListener listener) {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(requireContext(), (timeView, hourOfDay, minute) -> {
            String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
            listener.onDateTimeSet(time);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private interface OnDateTimeSetListener {
        void onDateTimeSet(String value);
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

    private void resetStatusChips(TextView... chips) {
        for (TextView chip : chips) {
            chip.setBackgroundResource(R.drawable.bg_chip_category_default);
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
