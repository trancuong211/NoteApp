package com.example.noteapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.noteapp.R;
import com.example.noteapp.model.Task;
import com.example.noteapp.viewmodel.TaskViewModel;

public class TaskDetailDialogFragment extends DialogFragment {

    private static final String ARG_TASK = "task";

    private Task task;
    private TaskViewModel taskViewModel;

    public static TaskDetailDialogFragment newInstance(Task task) {
        TaskDetailDialogFragment fragment = new TaskDetailDialogFragment();
        Bundle args = new Bundle();
        args.putInt("id", task.getId());
        args.putString("title", task.getTitle());
        args.putString("category", task.getCategory());
        args.putString("priority", task.getPriority());
        args.putString("status", task.getStatus());
        args.putBoolean("isDone", task.isDone());
        args.putString("startTime", task.getStartTime());
        args.putString("deadline", task.getDeadline());
        args.putString("dateKey", task.getDateKey());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_task_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);

        ImageView btnClose = view.findViewById(R.id.btn_close);
        TextView tvTitle = view.findViewById(R.id.tv_task_title);
        TextView tvStatus = view.findViewById(R.id.tv_task_status);
        TextView tvCategory = view.findViewById(R.id.tv_task_category);
        TextView tvPriority = view.findViewById(R.id.tv_task_priority);
        TextView btnCloseDialog = view.findViewById(R.id.btn_close_dialog);
        TextView btnDeleteTask = view.findViewById(R.id.btn_delete_task);
        TextView btnEditTask = view.findViewById(R.id.btn_edit_task);

        if (getArguments() != null) {
            String title = getArguments().getString("title");
            String category = getArguments().getString("category", "");
            String priority = getArguments().getString("priority", "");
            String status = getArguments().getString("status", "todo");
            boolean isDone = getArguments().getBoolean("isDone");

            tvTitle.setText(title);

            String statusLabel;
            if ("done".equals(status)) {
                statusLabel = "Hoàn thành";
            } else if ("inprogress".equals(status)) {
                statusLabel = "Đang làm";
            } else {
                statusLabel = "Chờ làm";
            }
            tvStatus.setText(statusLabel);
            if ("done".equals(status)) {
                tvStatus.setTextColor(getResources().getColor(R.color.tag_low_text));
                tvStatus.setBackgroundResource(R.drawable.bg_tag_low);
            } else if ("inprogress".equals(status)) {
                tvStatus.setTextColor(getResources().getColor(R.color.tag_medium_text));
                tvStatus.setBackgroundResource(R.drawable.bg_tag_medium);
            } else {
                tvStatus.setTextColor(getResources().getColor(R.color.tag_high_text));
                tvStatus.setBackgroundResource(R.drawable.bg_tag_high);
            }

            tvCategory.setText(getCategoryEmoji(category) + " " + category);
            tvCategory.setBackgroundResource(getCategoryBackground(category));
            tvCategory.setTextColor(getResources().getColor(getCategoryTextColor(category)));

            tvPriority.setText(getPriorityEmoji(priority) + " " + priority);
            tvPriority.setBackgroundResource(getPriorityBackground(priority));
            tvPriority.setTextColor(getResources().getColor(getPriorityTextColor(priority)));
        }

        btnClose.setOnClickListener(v -> dismiss());
        btnCloseDialog.setOnClickListener(v -> dismiss());

        btnEditTask.setOnClickListener(v -> {
            if (getArguments() != null) {
                Task taskToEdit = new Task();
                taskToEdit.setId(getArguments().getInt("id", 0));
                taskToEdit.setTitle(getArguments().getString("title", ""));
                taskToEdit.setCategory(getArguments().getString("category", ""));
                taskToEdit.setPriority(getArguments().getString("priority", ""));
                taskToEdit.setStatus(getArguments().getString("status", "todo"));
                taskToEdit.setStartTime(getArguments().getString("startTime", ""));
                taskToEdit.setDeadline(getArguments().getString("deadline", ""));
                taskToEdit.setDateKey(getArguments().getString("dateKey", ""));
                taskToEdit.setUserId(com.example.noteapp.util.UserManager.getUserId(requireContext()));

                dismiss();
                EditTaskDialogFragment editDialog = EditTaskDialogFragment.newInstance(taskToEdit);
                editDialog.show(getParentFragmentManager(), "EditTaskDialog");
            }
        });

        btnDeleteTask.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Xóa nhiệm vụ")
                    .setMessage("Bạn có chắc muốn xóa nhiệm vụ này?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        int taskId = getArguments() != null ? getArguments().getInt("id", 0) : 0;
                        String taskTitle = getArguments() != null ? getArguments().getString("title", "") : "";
                        Task taskToDelete = new Task();
                        taskToDelete.setId(taskId);
                        taskToDelete.setTitle(taskTitle);
                        taskViewModel.delete(taskToDelete);
                        dismiss();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    private String getCategoryEmoji(String category) {
        if (category == null) category = "";
        switch (category) {
            case "work": return "\uD83D\uDCBC";
            case "personal": return "\uD83D\uDC64";
            case "study": return "\uD83D\uDCDA";
            case "health": return "\u2764\uFE0F";
            default: return "\uD83D\uDCCB";
        }
    }

    private int getCategoryBackground(String category) {
        if (category == null) category = "";
        switch (category) {
            case "work": return R.drawable.bg_tag_work;
            case "personal": return R.drawable.bg_tag_personal;
            case "study": return R.drawable.bg_tag_study;
            case "health": return R.drawable.bg_tag_health;
            default: return R.drawable.bg_tag_work;
        }
    }

    private int getCategoryTextColor(String category) {
        if (category == null) category = "";
        switch (category) {
            case "work": return R.color.tag_work_text;
            case "personal": return R.color.tag_personal_text;
            case "study": return R.color.tag_study_text;
            case "health": return R.color.tag_health_text;
            default: return R.color.tag_work_text;
        }
    }

    private String getPriorityEmoji(String priority) {
        if (priority == null) priority = "";
        switch (priority) {
            case "high": return "\uD83D\uDD34";
            case "medium": return "\uD83D\uDFE0";
            case "low": return "\uD83D\uDFE2";
            default: return "\u26AA";
        }
    }

    private int getPriorityBackground(String priority) {
        if (priority == null) priority = "";
        switch (priority) {
            case "high": return R.drawable.bg_tag_high;
            case "medium": return R.drawable.bg_tag_medium;
            case "low": return R.drawable.bg_tag_low;
            default: return R.drawable.bg_tag_high;
        }
    }

    private int getPriorityTextColor(String priority) {
        if (priority == null) priority = "";
        switch (priority) {
            case "high": return R.color.tag_high_text;
            case "medium": return R.color.tag_medium_text;
            case "low": return R.color.tag_low_text;
            default: return R.color.tag_high_text;
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
