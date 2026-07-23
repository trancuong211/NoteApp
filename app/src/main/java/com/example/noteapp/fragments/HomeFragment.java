package com.example.noteapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.noteapp.R;
import com.example.noteapp.model.Reminder;
import com.example.noteapp.model.Task;
import com.example.noteapp.util.TaskScheduler;
import com.example.noteapp.util.UserManager;
import com.example.noteapp.viewmodel.NoteViewModel;
import com.example.noteapp.viewmodel.ReminderViewModel;
import com.example.noteapp.viewmodel.TaskViewModel;
import com.example.noteapp.viewmodel.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {

    private TaskViewModel taskViewModel;
    private NoteViewModel noteViewModel;
    private ReminderViewModel reminderViewModel;
    private UserViewModel userViewModel;

    private TextView tvGreeting;
    private TextView tvUserName;
    private TextView tvAvatar;
    private TextView tvProgressPercent;
    private TextView tvTasksSummary;
    private TextView tvPendingCount;
    private TextView tvDoneCount;
    private TextView tvRemindersCount;
    private ProgressBar progressBar;
    private TextView tvEmptyTasks;
    private TextView tvEmptyReminders;
    private RecyclerView rvUpcomingTasks;
    private RecyclerView rvActiveReminders;

    private MiniTaskAdapter miniTaskAdapter;
    private MiniReminderAdapter miniReminderAdapter;

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
        reminderViewModel = new ViewModelProvider(requireActivity()).get(ReminderViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        int userId = UserManager.getUserId(requireContext());
        taskViewModel.setUserId(userId);
        noteViewModel.setUserId(userId);
        reminderViewModel.setUserId(userId);

        initViews(view);
        setupGreeting();
        setupQuickActions(view);
        setupUpcomingTasks();
        setupActiveReminders();

        taskViewModel.getTasks().observe(getViewLifecycleOwner(), tasks -> {
            updateUpcomingTasks(tasks);
            updateProgressFromTasks(tasks);
        });
        reminderViewModel.getReminders().observe(getViewLifecycleOwner(), reminders -> {
            updateActiveReminders(reminders);
            updateRemindersCountFromList(reminders);
        });
    }

    private void initViews(View view) {
        tvGreeting = view.findViewById(R.id.tv_greeting);
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvAvatar = view.findViewById(R.id.tv_avatar);
        tvProgressPercent = view.findViewById(R.id.tv_progress_percent);
        tvTasksSummary = view.findViewById(R.id.tv_tasks_summary);
        tvPendingCount = view.findViewById(R.id.tv_pending_count);
        tvDoneCount = view.findViewById(R.id.tv_done_count);
        tvRemindersCount = view.findViewById(R.id.tv_reminders_count);
        progressBar = view.findViewById(R.id.progress_bar);
        tvEmptyTasks = view.findViewById(R.id.tv_empty_tasks);
        tvEmptyReminders = view.findViewById(R.id.tv_empty_reminders);
        rvUpcomingTasks = view.findViewById(R.id.rv_upcoming_tasks);
        rvActiveReminders = view.findViewById(R.id.rv_active_reminders);
    }

    private void setupGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting;
        if (hour < 12) {
            greeting = "Chào buổi sáng";
        } else if (hour < 18) {
            greeting = "Chào buổi chiều";
        } else {
            greeting = "Chào buổi tối";
        }
        tvGreeting.setText(greeting);

        int userId = UserManager.getUserId(requireContext());
        userViewModel.getById(userId, user -> {
            if (user != null && isAdded()) {
                String fullName = user.getFullName();
                if (fullName != null && !fullName.isEmpty()) {
                    tvUserName.setText(fullName);
                    String initials = getInitials(fullName);
                    tvAvatar.setText(initials);
                }
            }
        });
    }

    private String getInitials(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length >= 2) {
            return "" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0);
        } else if (parts.length == 1 && parts[0].length() > 0) {
            return "" + parts[0].charAt(0);
        }
        return "?";
    }

    private void setupQuickActions(View view) {
        view.findViewById(R.id.btn_add_task).setOnClickListener(v -> {
            NewTaskDialogFragment dialog = new NewTaskDialogFragment();
            dialog.show(getParentFragmentManager(), "NewTaskDialog");
        });

        view.findViewById(R.id.btn_calendar).setOnClickListener(v -> switchTab(R.id.nav_calendar));

        view.findViewById(R.id.btn_reminders).setOnClickListener(v -> switchTab(R.id.nav_reminder));

        view.findViewById(R.id.btn_tasks).setOnClickListener(v -> switchTab(R.id.nav_tasks));

        view.findViewById(R.id.btn_see_all_tasks).setOnClickListener(v -> switchTab(R.id.nav_tasks));

        view.findViewById(R.id.btn_see_all_reminders).setOnClickListener(v -> switchTab(R.id.nav_reminder));
    }

    private void switchTab(int itemId) {
        if (getActivity() == null) return;
        BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(itemId);
        }
    }

    private void setupUpcomingTasks() {
        miniTaskAdapter = new MiniTaskAdapter();
        rvUpcomingTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUpcomingTasks.setAdapter(miniTaskAdapter);
    }

    private void updateUpcomingTasks(List<Task> tasks) {
        List<Task> pending = new ArrayList<>();
        if (tasks != null) {
            for (Task task : tasks) {
                if (!task.isDone()) {
                    pending.add(task);
                }
            }
        }
        if (pending.size() > 3) {
            pending = pending.subList(0, 3);
        }
        miniTaskAdapter.setTasks(pending);
        if (pending.isEmpty()) {
            tvEmptyTasks.setVisibility(View.VISIBLE);
            rvUpcomingTasks.setVisibility(View.GONE);
        } else {
            tvEmptyTasks.setVisibility(View.GONE);
            rvUpcomingTasks.setVisibility(View.VISIBLE);
        }
    }

    private void setupActiveReminders() {
        miniReminderAdapter = new MiniReminderAdapter();
        rvActiveReminders.setLayoutManager(new LinearLayoutManager(getContext()));
        rvActiveReminders.setAdapter(miniReminderAdapter);
    }

    private void updateActiveReminders(List<Reminder> reminders) {
        List<Object> combined = new ArrayList<>();

        if (taskViewModel.getTasks() != null && taskViewModel.getTasks().getValue() != null) {
            for (Task task : taskViewModel.getTasks().getValue()) {
                if (task.isDone()) continue;
                if (task.getStartTime() != null && !task.getStartTime().isEmpty()) {
                    String timeUntil = TaskScheduler.getTimeUntil(task.getStartTime());
                    if (!timeUntil.isEmpty() && !timeUntil.equals("Đã quá hạn")) {
                        combined.add(task);
                    }
                }
                if (combined.size() >= 3) break;
            }
        }

        if (combined.size() < 3 && reminders != null) {
            for (Reminder r : reminders) {
                if (r.isActive()) {
                    combined.add(r);
                    if (combined.size() >= 3) break;
                }
            }
        }

        miniReminderAdapter.setMixedItems(combined);
        if (combined.isEmpty()) {
            tvEmptyReminders.setVisibility(View.VISIBLE);
            rvActiveReminders.setVisibility(View.GONE);
        } else {
            tvEmptyReminders.setVisibility(View.GONE);
            rvActiveReminders.setVisibility(View.VISIBLE);
        }
    }

    private void updateProgressFromTasks(List<Task> tasks) {
        int total = tasks != null ? tasks.size() : 0;
        int done = 0;
        if (tasks != null) {
            for (Task task : tasks) {
                if (task.isDone()) done++;
            }
        }
        int pending = total - done;

        tvTasksSummary.setText(done + "/" + total + " nhiệm vụ hoàn thành");
        tvPendingCount.setText(pending + " Chờ");
        tvDoneCount.setText(done + " Xong");

        int percent = total > 0 ? (done * 100 / total) : 0;
        tvProgressPercent.setText(percent + "%");
        if (progressBar != null) {
            progressBar.setProgress(percent);
        }
    }

    private void updateRemindersCountFromList(List<Reminder> reminders) {
        int active = 0;
        if (reminders != null) {
            for (Reminder r : reminders) {
                if (r.isActive()) active++;
            }
        }
        tvRemindersCount.setText(active + " Nhắc");
    }

    // Mini Task Adapter for home screen
    private class MiniTaskAdapter extends RecyclerView.Adapter<MiniTaskAdapter.ViewHolder> {
        private List<Task> tasks = new ArrayList<>();

        void setTasks(List<Task> tasks) {
            this.tasks = tasks;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_task, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Task task = tasks.get(position);
            holder.tvTaskTitle.setText(task.getTitle());

            if (task.getDeadline() != null && !task.getDeadline().isEmpty()) {
                holder.tvTaskDate.setText(task.getDeadline());
            } else if (task.getDateKey() != null && !task.getDateKey().isEmpty()) {
                holder.tvTaskDate.setText(task.getDateKey());
            } else {
                holder.tvTaskDate.setText("Không có hạn");
            }

            String priority = task.getPriority();
            if ("High".equals(priority)) {
                holder.tvPriorityBadge.setText("Cao");
                holder.tvPriorityBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.tag_high_text));
                holder.tvPriorityBadge.setBackgroundResource(R.drawable.bg_tag_high);
            } else if ("Medium".equals(priority)) {
                holder.tvPriorityBadge.setText("TB");
                holder.tvPriorityBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.tag_medium_text));
                holder.tvPriorityBadge.setBackgroundResource(R.drawable.bg_tag_medium);
            } else {
                holder.tvPriorityBadge.setText("Thấp");
                holder.tvPriorityBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.tag_low_text));
                holder.tvPriorityBadge.setBackgroundResource(R.drawable.bg_tag_low);
            }

            holder.circleIndicator.setBackgroundResource(task.isDone() ?
                    R.drawable.circle_task_checked : R.drawable.circle_task_unchecked);

            holder.itemView.setOnClickListener(v -> {
                TaskDetailDialogFragment dialog = TaskDetailDialogFragment.newInstance(task);
                dialog.show(getParentFragmentManager(), "TaskDetailDialog");
            });
        }

        @Override
        public int getItemCount() {
            return tasks.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            View circleIndicator;
            TextView tvTaskTitle;
            TextView tvTaskDate;
            TextView tvPriorityBadge;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                circleIndicator = itemView.findViewById(R.id.circle_indicator);
                tvTaskTitle = itemView.findViewById(R.id.tv_task_title);
                tvTaskDate = itemView.findViewById(R.id.tv_task_date);
                tvPriorityBadge = itemView.findViewById(R.id.tv_priority_badge);
            }
        }
    }

    // Mini Reminder Adapter for home screen (supports Task + Reminder mixed)
    private class MiniReminderAdapter extends RecyclerView.Adapter<MiniReminderAdapter.ViewHolder> {
        private List<Object> items = new ArrayList<>();

        void setMixedItems(List<Object> items) {
            this.items = items;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_reminder, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Object item = items.get(position);

            if (item instanceof Task) {
                Task task = (Task) item;
                holder.tvReminderTitle.setText("⏰ " + task.getTitle());
                String timeInfo = TaskScheduler.getTimeUntil(task.getStartTime());
                holder.tvReminderTime.setText("Bắt đầu: " + task.getStartTime() + " · " + timeInfo);
                holder.ivReminderIcon.setImageResource(R.drawable.ic_nav_calendar);
                int bgColor = ContextCompat.getColor(requireContext(), R.color.tag_work_bg);
                int iconColor = ContextCompat.getColor(requireContext(), R.color.tag_work_text);
                holder.ivReminderIcon.setColorFilter(iconColor);
                holder.ivReminderIcon.getBackground().setTint(bgColor);
                holder.dotActive.setVisibility(View.VISIBLE);
            } else if (item instanceof Reminder) {
                Reminder reminder = (Reminder) item;
                holder.tvReminderTitle.setText(reminder.getTitle());
                holder.tvReminderTime.setText(reminder.getTime() + " · " + reminder.getDate());

                String color = reminder.getColor();
                int bgColor;
                int iconColor;
                if ("red".equals(color)) {
                    bgColor = ContextCompat.getColor(requireContext(), R.color.tag_high_bg);
                    iconColor = ContextCompat.getColor(requireContext(), R.color.tag_high_text);
                } else if ("blue".equals(color)) {
                    bgColor = ContextCompat.getColor(requireContext(), R.color.tag_low_bg);
                    iconColor = ContextCompat.getColor(requireContext(), R.color.tag_low_text);
                } else if ("purple".equals(color)) {
                    bgColor = ContextCompat.getColor(requireContext(), R.color.tag_personal_bg);
                    iconColor = ContextCompat.getColor(requireContext(), R.color.tag_personal_text);
                } else {
                    bgColor = ContextCompat.getColor(requireContext(), R.color.tag_work_bg);
                    iconColor = ContextCompat.getColor(requireContext(), R.color.tag_work_text);
                }
                holder.ivReminderIcon.setColorFilter(iconColor);
                holder.ivReminderIcon.getBackground().setTint(bgColor);
                holder.dotActive.setVisibility(reminder.isActive() ? View.VISIBLE : View.INVISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivReminderIcon;
            TextView tvReminderTitle;
            TextView tvReminderTime;
            View dotActive;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivReminderIcon = itemView.findViewById(R.id.iv_reminder_icon);
                tvReminderTitle = itemView.findViewById(R.id.tv_reminder_title);
                tvReminderTime = itemView.findViewById(R.id.tv_reminder_time);
                dotActive = itemView.findViewById(R.id.dot_active);
            }
        }
    }
}
