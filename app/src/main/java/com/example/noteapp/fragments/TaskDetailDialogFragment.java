package com.example.noteapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.noteapp.R;
import com.example.noteapp.model.Task;

public class TaskDetailDialogFragment extends DialogFragment {

    private static final String ARG_TASK = "task";

    private Task task;

    public static TaskDetailDialogFragment newInstance(Task task) {
        TaskDetailDialogFragment fragment = new TaskDetailDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", task.getTitle());
        args.putString("category", task.getCategory());
        args.putString("priority", task.getPriority());
        args.putBoolean("isDone", task.isDone());
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

        ImageView btnClose = view.findViewById(R.id.btn_close);
        TextView tvTitle = view.findViewById(R.id.tv_task_title);
        TextView tvStatus = view.findViewById(R.id.tv_task_status);
        TextView tvCategory = view.findViewById(R.id.tv_task_category);
        TextView tvPriority = view.findViewById(R.id.tv_task_priority);
        TextView btnCloseDialog = view.findViewById(R.id.btn_close_dialog);

        if (getArguments() != null) {
            String title = getArguments().getString("title");
            String category = getArguments().getString("category");
            String priority = getArguments().getString("priority");
            boolean isDone = getArguments().getBoolean("isDone");

            tvTitle.setText(title);

            tvStatus.setText(isDone ? "Completed" : "Pending");
            tvStatus.setTextColor(getResources().getColor(isDone ? R.color.tag_low_text : R.color.tag_high_text));
            tvStatus.setBackgroundResource(isDone ? R.drawable.bg_tag_low : R.drawable.bg_tag_high);

            tvCategory.setText(getCategoryEmoji(category) + " " + category);
            tvCategory.setBackgroundResource(getCategoryBackground(category));
            tvCategory.setTextColor(getResources().getColor(getCategoryTextColor(category)));

            tvPriority.setText(getPriorityEmoji(priority) + " " + priority);
            tvPriority.setBackgroundResource(getPriorityBackground(priority));
            tvPriority.setTextColor(getResources().getColor(getPriorityTextColor(priority)));
        }

        btnClose.setOnClickListener(v -> dismiss());
        btnCloseDialog.setOnClickListener(v -> dismiss());
    }

    private String getCategoryEmoji(String category) {
        switch (category) {
            case "Work": return "💼";
            case "Personal": return "👤";
            case "Study": return "📚";
            case "Health": return "❤️";
            default: return "📋";
        }
    }

    private int getCategoryBackground(String category) {
        switch (category) {
            case "Work": return R.drawable.bg_tag_work;
            case "Personal": return R.drawable.bg_tag_personal;
            case "Study": return R.drawable.bg_tag_study;
            case "Health": return R.drawable.bg_tag_health;
            default: return R.drawable.bg_tag_work;
        }
    }

    private int getCategoryTextColor(String category) {
        switch (category) {
            case "Work": return R.color.tag_work_text;
            case "Personal": return R.color.tag_personal_text;
            case "Study": return R.color.tag_study_text;
            case "Health": return R.color.tag_health_text;
            default: return R.color.tag_work_text;
        }
    }

    private String getPriorityEmoji(String priority) {
        switch (priority) {
            case "High": return "🔴";
            case "Medium": return "🟠";
            case "Low": return "🟢";
            default: return "⚪";
        }
    }

    private int getPriorityBackground(String priority) {
        switch (priority) {
            case "High": return R.drawable.bg_tag_high;
            case "Medium": return R.drawable.bg_tag_medium;
            case "Low": return R.drawable.bg_tag_low;
            default: return R.drawable.bg_tag_high;
        }
    }

    private int getPriorityTextColor(String priority) {
        switch (priority) {
            case "High": return R.color.tag_high_text;
            case "Medium": return R.color.tag_medium_text;
            case "Low": return R.color.tag_low_text;
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
