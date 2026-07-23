package com.example.noteapp.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.R;
import com.example.noteapp.model.Task;
import com.example.noteapp.util.UserManager;
import com.example.noteapp.viewmodel.TaskViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.gridlayout.widget.GridLayout;

public class CalendarFragment extends Fragment {

    private TaskViewModel viewModel;
    private Calendar currentCalendar;
    private Calendar selectedCalendar;
    private TextView tvMonthYear;
    private TextView tvSelectedDate;
    private GridLayout calendarGrid;
    private RecyclerView rvDayTasks;
    private LinearLayout emptyStateLayout;
    private DayTaskAdapter taskAdapter;
    private List<Task> allTasks = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewModel();
        initViews(view);
        initCalendars();
        setupRecyclerView();
        setupNavigation();
        setupTaskObserver();

        updateCalendar();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
        viewModel.setUserId(UserManager.getUserId(requireContext()));
    }

    private void initViews(View view) {
        tvMonthYear = view.findViewById(R.id.tvMonthYear);
        tvSelectedDate = view.findViewById(R.id.tvSelectedDate);
        calendarGrid = view.findViewById(R.id.calendarGrid);
        rvDayTasks = view.findViewById(R.id.rvDayTasks);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
    }

    private void initCalendars() {
        currentCalendar = Calendar.getInstance();
        currentCalendar.set(Calendar.DAY_OF_MONTH, 1);

        selectedCalendar = Calendar.getInstance();
        selectedCalendar.set(Calendar.HOUR_OF_DAY, 0);
        selectedCalendar.set(Calendar.MINUTE, 0);
        selectedCalendar.set(Calendar.SECOND, 0);
        selectedCalendar.set(Calendar.MILLISECOND, 0);
    }

    private void setupRecyclerView() {
        taskAdapter = new DayTaskAdapter();
        rvDayTasks.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvDayTasks.setAdapter(taskAdapter);
    }

    private void setupNavigation() {
        ImageButton btnPrevMonth = requireView().findViewById(R.id.btnPrevMonth);
        ImageButton btnNextMonth = requireView().findViewById(R.id.btnNextMonth);

        btnPrevMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            updateCalendar();
        });

        btnNextMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            updateCalendar();
        });
    }

    private void setupTaskObserver() {
        viewModel.getTasks().observe(getViewLifecycleOwner(), tasks -> {
            allTasks = tasks;
            updateCalendar();
            updateDayTasks();
        });
    }

    private void updateCalendar() {
        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH);

        String monthName = getMonthName(month);
        tvMonthYear.setText(monthName + " - " + year);

        buildCalendarGrid(year, month);
    }

    private void buildCalendarGrid(int year, int month) {
        calendarGrid.removeAllViews();

        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.set(year, month, 1);

        int firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        int daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        for (int i = 0; i < firstDayOfWeek; i++) {
            View emptyView = createEmptyDayView();
            calendarGrid.addView(emptyView);
        }

        for (int day = 1; day <= daysInMonth; day++) {
            final int dayOfMonth = day;
            Calendar dayCalendar = Calendar.getInstance();
            dayCalendar.set(year, month, dayOfMonth);
            dayCalendar.set(Calendar.HOUR_OF_DAY, 0);
            dayCalendar.set(Calendar.MINUTE, 0);
            dayCalendar.set(Calendar.SECOND, 0);
            dayCalendar.set(Calendar.MILLISECOND, 0);

            boolean isToday = dayCalendar.equals(today);
            boolean isSelected = dayCalendar.equals(selectedCalendar);

            List<Task> dayTasks = getTasksForDate(dayCalendar);
            boolean hasTasks = !dayTasks.isEmpty();
            boolean allTasksDone = hasTasks && allTasksCompleted(dayTasks);

            View dayView = createDayView(dayOfMonth, isToday, isSelected, hasTasks, allTasksDone);
            dayView.setOnClickListener(v -> {
                selectedCalendar.set(year, month, dayOfMonth);
                selectedCalendar.set(Calendar.HOUR_OF_DAY, 0);
                selectedCalendar.set(Calendar.MINUTE, 0);
                selectedCalendar.set(Calendar.SECOND, 0);
                selectedCalendar.set(Calendar.MILLISECOND, 0);
                updateCalendar();
                updateDayTasks();
            });

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(2, 4, 2, 4);
            dayView.setLayoutParams(params);

            calendarGrid.addView(dayView);
        }
    }

    private View createEmptyDayView() {
        View view = new View(requireContext());
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(2, 4, 2, 4);
        view.setLayoutParams(params);
        return view;
    }

    private View createDayView(int day, boolean isToday, boolean isSelected,
                               boolean hasTasks, boolean allTasksDone) {
        LinearLayout container = new LinearLayout(requireContext());
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(android.view.Gravity.CENTER);
        container.setPadding(4, 8, 4, 8);

        if (isToday) {
            container.setBackgroundResource(R.drawable.bg_today);
        } else if (isSelected) {
            container.setBackgroundResource(R.drawable.bg_selected_date);
        }

        TextView dayText = new TextView(requireContext());
        dayText.setText(String.valueOf(day));
        dayText.setGravity(android.view.Gravity.CENTER);
        dayText.setTextSize(14);

        if (isToday || isSelected) {
            dayText.setTextColor(Color.WHITE);
            dayText.setTypeface(null, Typeface.BOLD);
        } else {
            dayText.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));
        }

        container.addView(dayText);

        if (hasTasks) {
            LinearLayout dotsContainer = new LinearLayout(requireContext());
            dotsContainer.setOrientation(LinearLayout.HORIZONTAL);
            dotsContainer.setGravity(android.view.Gravity.CENTER);
            dotsContainer.setPadding(0, 2, 0, 0);

            int dotSize = dpToPx(4);
            int dotMargin = dpToPx(2);

            View dot = new View(requireContext());
            LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(dotSize, dotSize);
            dotParams.setMargins(dotMargin, 0, dotMargin, 0);
            dot.setLayoutParams(dotParams);

            if (allTasksDone) {
                dot.setBackgroundResource(R.drawable.bg_dot_green);
            } else {
                dot.setBackgroundResource(R.drawable.bg_dot_gray);
            }

            dotsContainer.addView(dot);
            container.addView(dotsContainer);
        }

        return container;
    }

    private void updateDayTasks() {
        List<Task> dayTasks = getTasksForDate(selectedCalendar);

        int day = selectedCalendar.get(Calendar.DAY_OF_MONTH);
        int month = selectedCalendar.get(Calendar.MONTH) + 1;
        int year = selectedCalendar.get(Calendar.YEAR);
        tvSelectedDate.setText("Nhiệm vụ ngày " + day + "/" + month + "/" + year);

        if (dayTasks.isEmpty()) {
            rvDayTasks.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            rvDayTasks.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
            taskAdapter.setTasks(dayTasks);
        }
    }

    private List<Task> getTasksForDate(Calendar date) {
        List<Task> tasksForDate = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String targetDate = dateFormat.format(date.getTime());

        for (Task task : allTasks) {
            if (task.getDateKey() != null && task.getDateKey().equals(targetDate)) {
                tasksForDate.add(task);
            }
        }

        return tasksForDate;
    }

    private boolean allTasksCompleted(List<Task> tasks) {
        for (Task task : tasks) {
            if (!task.isDone()) {
                return false;
            }
        }
        return true;
    }

    private String getMonthName(int month) {
        String[] months = {
            "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4",
            "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8",
            "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"
        };
        return months[month];
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private class DayTaskAdapter extends RecyclerView.Adapter<DayTaskAdapter.ViewHolder> {
        private List<Task> tasks = new ArrayList<>();

        void setTasks(List<Task> tasks) {
            this.tasks = tasks;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_task_calendar, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Task task = tasks.get(position);
            holder.bind(task);
        }

        @Override
        public int getItemCount() {
            return tasks.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final View statusIndicator;
            private final TextView tvTaskTitle;
            private final TextView tvCategory;
            private final TextView tvPriority;
            private final TextView tvTime;
            private final ImageView ivTaskStatus;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                statusIndicator = itemView.findViewById(R.id.statusIndicator);
                tvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
                tvCategory = itemView.findViewById(R.id.tvCategory);
                tvPriority = itemView.findViewById(R.id.tvPriority);
                tvTime = itemView.findViewById(R.id.tvTime);
                ivTaskStatus = itemView.findViewById(R.id.ivTaskStatus);
            }

            void bind(Task task) {
                tvTaskTitle.setText(task.getTitle());

                // Status indicator
                android.graphics.drawable.GradientDrawable statusDrawable = new android.graphics.drawable.GradientDrawable();
                statusDrawable.setShape(android.graphics.drawable.GradientDrawable.OVAL);
                if (task.isDone()) {
                    statusDrawable.setColor(android.graphics.Color.parseColor("#00D68F"));
                } else if ("inprogress".equals(task.getStatus())) {
                    statusDrawable.setColor(android.graphics.Color.parseColor("#F59E0B"));
                } else {
                    statusDrawable.setStroke(2, android.graphics.Color.parseColor("#6B6B9A"));
                    statusDrawable.setColor(android.graphics.Color.TRANSPARENT);
                }
                statusIndicator.setBackground(statusDrawable);

                // Title style
                if (task.isDone()) {
                    tvTaskTitle.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_secondary));
                    tvTaskTitle.setPaintFlags(tvTaskTitle.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    tvTaskTitle.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_primary));
                    tvTaskTitle.setPaintFlags(tvTaskTitle.getPaintFlags() & ~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                }

                // Category
                String category = task.getCategory() != null ? task.getCategory() : "Work";
                tvCategory.setText(getCategoryLabel(category));
                tvCategory.setBackgroundResource(getCategoryBackground(category));
                tvCategory.setTextColor(ContextCompat.getColor(itemView.getContext(), getCategoryTextColor(category)));

                // Priority
                String priority = task.getPriority() != null ? task.getPriority() : "Medium";
                tvPriority.setText(getPriorityLabel(priority));
                tvPriority.setBackgroundResource(getPriorityBackground(priority));
                tvPriority.setTextColor(ContextCompat.getColor(itemView.getContext(), getPriorityTextColor(priority)));

                // Time
                String timeInfo = "";
                if (task.getStartTime() != null && !task.getStartTime().isEmpty()) {
                    timeInfo = task.getStartTime();
                }
                if (task.getDeadline() != null && !task.getDeadline().isEmpty()) {
                    if (!timeInfo.isEmpty()) timeInfo += " → ";
                    timeInfo += task.getDeadline();
                }
                if (timeInfo.isEmpty()) {
                    tvTime.setVisibility(View.GONE);
                } else {
                    tvTime.setVisibility(View.VISIBLE);
                    tvTime.setText(timeInfo);
                }

                // Status icon
                if (task.isDone()) {
                    ivTaskStatus.setImageResource(android.R.drawable.ic_menu_send);
                    ivTaskStatus.setColorFilter(android.graphics.Color.parseColor("#00D68F"));
                } else {
                    ivTaskStatus.setImageResource(android.R.drawable.ic_menu_agenda);
                    ivTaskStatus.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.text_secondary));
                }
            }

            private String getCategoryLabel(String category) {
                switch (category) {
                    case "work": case "Work": return "💼 Công việc";
                    case "personal": case "Personal": return "👤 Cá nhân";
                    case "study": case "Study": return "📚 Học tập";
                    case "health": case "Health": return "❤️ Sức khỏe";
                    default: return "📋 Khác";
                }
            }

            private int getCategoryBackground(String category) {
                switch (category) {
                    case "work": case "Work": return R.drawable.bg_tag_work;
                    case "personal": case "Personal": return R.drawable.bg_tag_personal;
                    case "study": case "Study": return R.drawable.bg_tag_study;
                    case "health": case "Health": return R.drawable.bg_tag_health;
                    default: return R.drawable.bg_tag_work;
                }
            }

            private int getCategoryTextColor(String category) {
                switch (category) {
                    case "work": case "Work": return R.color.tag_work_text;
                    case "personal": case "Personal": return R.color.tag_personal_text;
                    case "study": case "Study": return R.color.tag_study_text;
                    case "health": case "Health": return R.color.tag_health_text;
                    default: return R.color.tag_work_text;
                }
            }

            private String getPriorityLabel(String priority) {
                switch (priority) {
                    case "high": case "High": return "🔴 Cao";
                    case "medium": case "Medium": return "🟠 TB";
                    case "low": case "Low": return "🟢 Thấp";
                    default: return "⚪ TB";
                }
            }

            private int getPriorityBackground(String priority) {
                switch (priority) {
                    case "high": case "High": return R.drawable.bg_tag_high;
                    case "medium": case "Medium": return R.drawable.bg_tag_medium;
                    case "low": case "Low": return R.drawable.bg_tag_low;
                    default: return R.drawable.bg_tag_medium;
                }
            }

            private int getPriorityTextColor(String priority) {
                switch (priority) {
                    case "high": case "High": return R.color.tag_high_text;
                    case "medium": case "Medium": return R.color.tag_medium_text;
                    case "low": case "Low": return R.color.tag_low_text;
                    default: return R.color.tag_medium_text;
                }
            }
        }
    }
}
