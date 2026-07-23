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
import com.example.noteapp.util.TaskScheduler;
import com.example.noteapp.util.UserManager;
import com.example.noteapp.viewmodel.TaskViewModel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewTaskDialogFragment extends DialogFragment {

    private String selectedCategory = "work";
    private String selectedPriority = "medium";
    private String selectedStatus = "todo";
    private TaskViewModel taskViewModel;

    private String deadlineDate = "";
    private String deadlineTime = "";
    private String startDate = "";
    private String startTime = "";

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

        EditText etDeadlineDate = view.findViewById(R.id.et_deadline_date);
        EditText etDeadlineTime = view.findViewById(R.id.et_deadline_time);
        EditText etStartDate = view.findViewById(R.id.et_start_date);
        EditText etStartTime = view.findViewById(R.id.et_start_time);

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

        btnAddTask.setOnClickListener(v -> {
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
            if (!startDate.isEmpty()) {
                start = startDate;
                if (!startTime.isEmpty()) {
                    start += " " + startTime;
                }
            }

            String dateKey = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            Task newTask = new Task(title, selectedCategory, selectedPriority, selectedStatus, start, deadline, dateKey);
            newTask.setUserId(UserManager.getUserId(requireContext()));
            taskViewModel.insert(newTask);

            if (!deadline.isEmpty()) {
                int notificationId = (int) System.currentTimeMillis();
                TaskScheduler.scheduleTaskReminder(requireContext(), notificationId, title, deadline, "deadline");
            }

            if (!start.isEmpty()) {
                int notificationId = (int) (System.currentTimeMillis() + 1);
                TaskScheduler.scheduleTaskReminder(requireContext(), notificationId, title, start, "start");
            }

            dismiss();
        });
    }

    private void showDatePicker(OnDateTimeSetListener listener) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            String date = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
            listener.onDateTimeSet(date);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker(OnDateTimeSetListener listener) {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
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
