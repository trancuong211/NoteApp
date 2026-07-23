package com.example.noteapp.fragments;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.noteapp.R;
import com.example.noteapp.model.Task;
import com.example.noteapp.util.UserManager;
import com.example.noteapp.viewmodel.TaskViewModel;

import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends Fragment {

    private TaskViewModel taskViewModel;
    private TextView tvEmptyState;
    private LinearLayout groupedSections;

    private String selectedCategory = null;
    private String selectedStatus = null;

    private TextView chipCatAll, chipCatWork, chipCatPersonal, chipCatStudy, chipCatHealth;
    private TextView chipStatusAll, chipStatusTodo, chipStatusInprogress, chipStatusDone;

    private LinearLayout groupInprogress, groupTodo, groupDone;
    private LinearLayout tasksInprogress, tasksTodo, tasksDone;
    private TextView countInprogress, countTodo, countDone;
    private TextView toggleInProgress, toggleTodo, toggleDone;
    private View headerInprogress, headerTodo, headerDone;

    private List<Task> allTasks = new ArrayList<>();

    private boolean inprogressCollapsed = false;
    private boolean todoCollapsed = false;
    private boolean doneCollapsed = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
        taskViewModel.setUserId(UserManager.getUserId(requireContext()));

        initViews(view);
        setupCategoryChips();
        setupStatusChips();
        setupGroupHeaders();
        setupAddButton(view);

        taskViewModel.getTasks().observe(getViewLifecycleOwner(), tasks -> {
            allTasks = tasks != null ? tasks : new ArrayList<>();
            applyFilters();
        });
    }

    private void initViews(View view) {
        tvEmptyState = view.findViewById(R.id.tv_empty_state);
        groupedSections = view.findViewById(R.id.grouped_sections);

        chipCatAll = view.findViewById(R.id.chip_cat_all);
        chipCatWork = view.findViewById(R.id.chip_cat_work);
        chipCatPersonal = view.findViewById(R.id.chip_cat_personal);
        chipCatStudy = view.findViewById(R.id.chip_cat_study);
        chipCatHealth = view.findViewById(R.id.chip_cat_health);

        chipStatusAll = view.findViewById(R.id.chip_status_all);
        chipStatusTodo = view.findViewById(R.id.chip_status_todo);
        chipStatusInprogress = view.findViewById(R.id.chip_status_inprogress);
        chipStatusDone = view.findViewById(R.id.chip_status_done);

        groupInprogress = view.findViewById(R.id.group_inprogress);
        groupTodo = view.findViewById(R.id.group_todo);
        groupDone = view.findViewById(R.id.group_done);

        tasksInprogress = view.findViewById(R.id.tasks_inprogress);
        tasksTodo = view.findViewById(R.id.tasks_todo);
        tasksDone = view.findViewById(R.id.tasks_done);

        countInprogress = view.findViewById(R.id.count_inprogress);
        countTodo = view.findViewById(R.id.count_todo);
        countDone = view.findViewById(R.id.count_done);

        toggleInProgress = view.findViewById(R.id.toggle_inprogress);
        toggleTodo = view.findViewById(R.id.toggle_todo);
        toggleDone = view.findViewById(R.id.toggle_done);

        headerInprogress = view.findViewById(R.id.header_inprogress);
        headerTodo = view.findViewById(R.id.header_todo);
        headerDone = view.findViewById(R.id.header_done);
    }

    private void setupAddButton(View view) {
        TextView btnAdd = view.findViewById(R.id.btn_add);
        if (btnAdd != null) {
            btnAdd.setOnClickListener(v -> {
                NewTaskDialogFragment dialog = new NewTaskDialogFragment();
                dialog.show(getParentFragmentManager(), "NewTaskDialog");
            });
        }
    }

    private void setupCategoryChips() {
        TextView[] allChips = {chipCatAll, chipCatWork, chipCatPersonal, chipCatStudy, chipCatHealth};
        View.OnClickListener listener = v -> {
            int id = v.getId();
            if (id == R.id.chip_cat_all) {
                selectedCategory = null;
            } else if (id == R.id.chip_cat_work) {
                selectedCategory = "Work";
            } else if (id == R.id.chip_cat_personal) {
                selectedCategory = "Personal";
            } else if (id == R.id.chip_cat_study) {
                selectedCategory = "Study";
            } else if (id == R.id.chip_cat_health) {
                selectedCategory = "Health";
            }
            updateCategoryChips();
            applyFilters();
        };
        for (TextView chip : allChips) {
            chip.setOnClickListener(listener);
        }
    }

    private void updateCategoryChips() {
        TextView[] chips = {chipCatAll, chipCatWork, chipCatPersonal, chipCatStudy, chipCatHealth};
        boolean[] selected = {
                selectedCategory == null,
                "Work".equals(selectedCategory),
                "Personal".equals(selectedCategory),
                "Study".equals(selectedCategory),
                "Health".equals(selectedCategory)
        };
        for (int i = 0; i < chips.length; i++) {
            if (selected[i]) {
                chips[i].setBackgroundResource(R.drawable.bg_chip_selected);
                chips[i].setTextColor(getResources().getColor(R.color.text_primary));
            } else {
                chips[i].setBackgroundResource(R.drawable.bg_chip_default);
                chips[i].setTextColor(getResources().getColor(R.color.text_secondary));
            }
        }
    }

    private void setupStatusChips() {
        TextView[] allChips = {chipStatusAll, chipStatusTodo, chipStatusInprogress, chipStatusDone};
        View.OnClickListener listener = v -> {
            int id = v.getId();
            if (id == R.id.chip_status_all) {
                selectedStatus = null;
            } else if (id == R.id.chip_status_todo) {
                selectedStatus = "todo";
            } else if (id == R.id.chip_status_inprogress) {
                selectedStatus = "inprogress";
            } else if (id == R.id.chip_status_done) {
                selectedStatus = "done";
            }
            updateStatusChips();
            applyFilters();
        };
        for (TextView chip : allChips) {
            chip.setOnClickListener(listener);
        }
    }

    private void updateStatusChips() {
        TextView[] chips = {chipStatusAll, chipStatusTodo, chipStatusInprogress, chipStatusDone};
        boolean[] selected = {
                selectedStatus == null,
                "todo".equals(selectedStatus),
                "inprogress".equals(selectedStatus),
                "done".equals(selectedStatus)
        };
        for (int i = 0; i < chips.length; i++) {
            if (selected[i]) {
                chips[i].setBackgroundResource(R.drawable.bg_chip_selected);
                chips[i].setTextColor(getResources().getColor(R.color.text_primary));
            } else {
                chips[i].setBackgroundResource(R.drawable.bg_chip_default);
                chips[i].setTextColor(getResources().getColor(R.color.text_secondary));
            }
        }
    }

    private void setupGroupHeaders() {
        headerInprogress.setOnClickListener(v -> {
            inprogressCollapsed = !inprogressCollapsed;
            tasksInprogress.setVisibility(inprogressCollapsed ? View.GONE : View.VISIBLE);
            toggleInProgress.setText(inprogressCollapsed ? "▶" : "▼");
        });
        headerTodo.setOnClickListener(v -> {
            todoCollapsed = !todoCollapsed;
            tasksTodo.setVisibility(todoCollapsed ? View.GONE : View.VISIBLE);
            toggleTodo.setText(todoCollapsed ? "▶" : "▼");
        });
        headerDone.setOnClickListener(v -> {
            doneCollapsed = !doneCollapsed;
            tasksDone.setVisibility(doneCollapsed ? View.GONE : View.VISIBLE);
            toggleDone.setText(doneCollapsed ? "▶" : "▼");
        });
    }

    private void applyFilters() {
        List<Task> filtered = new ArrayList<>();
        for (Task task : allTasks) {
            if (selectedCategory != null && !selectedCategory.equals(task.getCategory())) {
                continue;
            }
            if (selectedStatus != null && !selectedStatus.equals(task.getStatus())) {
                continue;
            }
            filtered.add(task);
        }

        List<Task> inprogressList = new ArrayList<>();
        List<Task> todoList = new ArrayList<>();
        List<Task> doneList = new ArrayList<>();

        for (Task task : filtered) {
            String status = task.getStatus();
            if ("inprogress".equals(status)) {
                inprogressList.add(task);
            } else if ("done".equals(status)) {
                doneList.add(task);
            } else {
                todoList.add(task);
            }
        }

        if (filtered.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            groupedSections.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            groupedSections.setVisibility(View.VISIBLE);
        }

        renderGroup(tasksInprogress, inprogressList, countInprogress, groupInprogress);
        renderGroup(tasksTodo, todoList, countTodo, groupTodo);
        renderGroup(tasksDone, doneList, countDone, groupDone);

        groupInprogress.setVisibility(inprogressList.isEmpty() ? View.GONE : View.VISIBLE);
        groupTodo.setVisibility(todoList.isEmpty() ? View.GONE : View.VISIBLE);
        groupDone.setVisibility(doneList.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void renderGroup(LinearLayout container, List<Task> tasks, TextView countView, LinearLayout group) {
        container.removeAllViews();
        countView.setText(String.valueOf(tasks.size()));

        for (Task task : tasks) {
            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_task, container, false);
            bindTaskView(itemView, task);
            container.addView(itemView);
        }
    }

    private void bindTaskView(View itemView, Task task) {
        View statusIndicator = itemView.findViewById(R.id.status_indicator);
        TextView tvTitle = itemView.findViewById(R.id.tv_task_title);
        TextView tvCategory = itemView.findViewById(R.id.tv_task_category);
        TextView tvPriority = itemView.findViewById(R.id.tv_task_priority);
        ImageView btnDelete = itemView.findViewById(R.id.btn_delete);

        tvTitle.setText(task.getTitle());

        tvCategory.setText(getCategoryEmoji(task.getCategory()) + " " + task.getCategory());
        tvCategory.setBackgroundResource(getCategoryBackground(task.getCategory()));
        tvCategory.setTextColor(getResources().getColor(getCategoryTextColor(task.getCategory())));

        tvPriority.setText(getPriorityEmoji(task.getPriority()) + " " + task.getPriority());
        tvPriority.setBackgroundResource(getPriorityBackground(task.getPriority()));
        tvPriority.setTextColor(getResources().getColor(getPriorityTextColor(task.getPriority())));

        android.graphics.drawable.GradientDrawable statusDrawable = new android.graphics.drawable.GradientDrawable();
        statusDrawable.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        switch (task.getStatus()) {
            case "done":
                statusDrawable.setColor(android.graphics.Color.parseColor("#00D68F"));
                break;
            case "inprogress":
                statusDrawable.setColor(android.graphics.Color.parseColor("#F59E0B"));
                break;
            default:
                statusDrawable.setStroke(2, android.graphics.Color.parseColor("#6B6B9A"));
                statusDrawable.setColor(android.graphics.Color.TRANSPARENT);
                break;
        }
        statusIndicator.setBackground(statusDrawable);

        if (task.isDone()) {
            tvTitle.setPaintFlags(tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tvTitle.setTextColor(getResources().getColor(R.color.text_muted));
        } else {
            tvTitle.setPaintFlags(tvTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            tvTitle.setTextColor(getResources().getColor(R.color.text_primary));
        }

        statusIndicator.setOnClickListener(v -> taskViewModel.setTaskStatus(
                task, task.isDone() ? "todo" : "done"));

        btnDelete.setOnClickListener(v -> taskViewModel.delete(task));

        itemView.setOnClickListener(v -> {
            TaskDetailDialogFragment dialog = TaskDetailDialogFragment.newInstance(task);
            dialog.show(getParentFragmentManager(), "TaskDetailDialog");
        });
    }

    private String getCategoryEmoji(String category) {
        switch (category) {
            case "Work": return "\uD83D\uDCBC";
            case "Personal": return "\uD83D\uDC64";
            case "Study": return "\uD83D\uDCDA";
            case "Health": return "\u2764\uFE0F";
            default: return "\uD83D\uDCCB";
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
            case "High": return "\uD83D\uDD34";
            case "Medium": return "\uD83D\uDFE0";
            case "Low": return "\uD83D\uDFE2";
            default: return "\u26AA";
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
}
