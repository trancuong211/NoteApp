package com.example.noteapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.noteapp.R;
import com.example.noteapp.adapter.ReminderAdapter;
import com.example.noteapp.model.Reminder;
import com.example.noteapp.util.UserManager;
import com.example.noteapp.viewmodel.ReminderViewModel;
import java.util.ArrayList;
import java.util.List;

public class ReminderFragment extends Fragment {

    private ReminderViewModel reminderViewModel;
    private ReminderAdapter activeAdapter;
    private ReminderAdapter inactiveAdapter;
    private ReminderAdapter queueAdapter;

    private TextView tvTotalCount;
    private TextView tvActiveCount;
    private TextView tvInactiveCount;
    private TextView tvActiveBadge;
    private TextView tvActiveSectionCount;
    private TextView tvInactiveSectionCount;
    private TextView tvQueueSectionCount;
    private TextView tvEmptyState;
    private LinearLayout layoutActiveSection;
    private LinearLayout layoutInactiveSection;
    private LinearLayout layoutQueueSection;
    private RecyclerView rvActiveReminders;
    private RecyclerView rvInactiveReminders;
    private RecyclerView rvQueueReminders;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reminder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        reminderViewModel = new ViewModelProvider(requireActivity()).get(ReminderViewModel.class);
        reminderViewModel.setUserId(UserManager.getUserId(requireContext()));

        tvTotalCount = view.findViewById(R.id.tv_total_count);
        tvActiveCount = view.findViewById(R.id.tv_active_count);
        tvInactiveCount = view.findViewById(R.id.tv_inactive_count);
        tvActiveBadge = view.findViewById(R.id.tv_active_badge);
        tvActiveSectionCount = view.findViewById(R.id.tv_active_section_count);
        tvInactiveSectionCount = view.findViewById(R.id.tv_inactive_section_count);
        tvQueueSectionCount = view.findViewById(R.id.tv_queue_section_count);
        tvEmptyState = view.findViewById(R.id.tv_empty_state);
        layoutActiveSection = view.findViewById(R.id.layout_active_section);
        layoutInactiveSection = view.findViewById(R.id.layout_inactive_section);
        layoutQueueSection = view.findViewById(R.id.layout_queue_section);
        rvActiveReminders = view.findViewById(R.id.rv_active_reminders);
        rvInactiveReminders = view.findViewById(R.id.rv_inactive_reminders);
        rvQueueReminders = view.findViewById(R.id.rv_queue_reminders);

        rvActiveReminders.setLayoutManager(new LinearLayoutManager(getContext()));
        rvInactiveReminders.setLayoutManager(new LinearLayoutManager(getContext()));
        rvQueueReminders.setLayoutManager(new LinearLayoutManager(getContext()));

        TextView btnAddReminder = view.findViewById(R.id.btn_add_reminder);
        btnAddReminder.setOnClickListener(v -> {
            NewReminderDialogFragment dialog = new NewReminderDialogFragment();
            dialog.show(getParentFragmentManager(), "NewReminderDialog");
        });

        ReminderAdapter.OnReminderClickListener listener = new ReminderAdapter.OnReminderClickListener() {
            @Override
            public void onToggle(Reminder reminder) {
                reminderViewModel.toggleReminder(reminder);
            }

            @Override
            public void onDelete(int id) {
                reminderViewModel.deleteById(id);
            }
        };

        activeAdapter = new ReminderAdapter(listener);
        inactiveAdapter = new ReminderAdapter(listener);
        queueAdapter = new ReminderAdapter(listener);

        rvActiveReminders.setAdapter(activeAdapter);
        rvInactiveReminders.setAdapter(inactiveAdapter);
        rvQueueReminders.setAdapter(queueAdapter);

        reminderViewModel.getReminders().observe(getViewLifecycleOwner(), reminders -> {
            List<Reminder> activeList = new ArrayList<>();
            List<Reminder> inactiveList = new ArrayList<>();
            List<Reminder> queueList = new ArrayList<>();

            if (reminders != null) {
                for (Reminder r : reminders) {
                    if (r.isActive()) {
                        activeList.add(r);
                        if (isScheduledForToday(r)) {
                            queueList.add(r);
                        }
                    } else {
                        inactiveList.add(r);
                    }
                }
            }

            int totalCount = reminders != null ? reminders.size() : 0;
            int activeCount = activeList.size();
            int inactiveCount = inactiveList.size();
            int queueCount = queueList.size();

            tvTotalCount.setText(String.valueOf(totalCount));
            tvActiveCount.setText(String.valueOf(activeCount));
            tvInactiveCount.setText(String.valueOf(inactiveCount));
            tvActiveBadge.setText(activeCount + " đang bật");
            tvActiveSectionCount.setText(String.valueOf(activeCount));
            tvInactiveSectionCount.setText(String.valueOf(inactiveCount));
            tvQueueSectionCount.setText(String.valueOf(queueCount));

            activeAdapter.setReminders(activeList);
            inactiveAdapter.setReminders(inactiveList);
            queueAdapter.setReminders(queueList);

            if (totalCount == 0) {
                tvEmptyState.setVisibility(View.VISIBLE);
                layoutActiveSection.setVisibility(View.GONE);
                rvActiveReminders.setVisibility(View.GONE);
                layoutInactiveSection.setVisibility(View.GONE);
                rvInactiveReminders.setVisibility(View.GONE);
                layoutQueueSection.setVisibility(View.GONE);
                rvQueueReminders.setVisibility(View.GONE);
            } else {
                tvEmptyState.setVisibility(View.GONE);

                if (queueCount > 0) {
                    layoutQueueSection.setVisibility(View.VISIBLE);
                    rvQueueReminders.setVisibility(View.VISIBLE);
                } else {
                    layoutQueueSection.setVisibility(View.GONE);
                    rvQueueReminders.setVisibility(View.GONE);
                }

                if (activeCount > 0) {
                    layoutActiveSection.setVisibility(View.VISIBLE);
                    rvActiveReminders.setVisibility(View.VISIBLE);
                } else {
                    layoutActiveSection.setVisibility(View.GONE);
                    rvActiveReminders.setVisibility(View.GONE);
                }

                if (inactiveCount > 0) {
                    layoutInactiveSection.setVisibility(View.VISIBLE);
                    rvInactiveReminders.setVisibility(View.VISIBLE);
                } else {
                    layoutInactiveSection.setVisibility(View.GONE);
                    rvInactiveReminders.setVisibility(View.GONE);
                }
            }
        });
    }

    private boolean isScheduledForToday(Reminder reminder) {
        if (reminder.getTime() == null || reminder.getTime().isEmpty()) {
            return false;
        }

        java.util.Calendar today = java.util.Calendar.getInstance();
        int dayOfWeek = today.get(java.util.Calendar.DAY_OF_WEEK);
        int dayOfMonth = today.get(java.util.Calendar.DAY_OF_MONTH);

        String repeat = reminder.getRepeat();
        if (repeat == null) repeat = "";

        boolean matchesRepeat = false;
        switch (repeat) {
            case "Một lần":
                String reminderDate = reminder.getDate();
                if (reminderDate != null && !reminderDate.isEmpty()) {
                    try {
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
                        java.util.Date d = sdf.parse(reminderDate);
                        if (d != null) {
                            java.util.Calendar cal = java.util.Calendar.getInstance();
                            cal.setTime(d);
                            matchesRepeat = (cal.get(java.util.Calendar.DAY_OF_MONTH) == dayOfMonth);
                        }
                    } catch (java.text.ParseException e) {
                        matchesRepeat = false;
                    }
                }
                break;
            case "Hàng ngày":
                matchesRepeat = true;
                break;
            case "Ngày làm việc":
                matchesRepeat = (dayOfWeek >= java.util.Calendar.MONDAY && dayOfWeek <= java.util.Calendar.FRIDAY);
                break;
            case "Cuối tuần":
                matchesRepeat = (dayOfWeek == java.util.Calendar.SATURDAY || dayOfWeek == java.util.Calendar.SUNDAY);
                break;
            case "Hàng tuần":
                matchesRepeat = true;
                break;
            case "Hàng tháng":
                matchesRepeat = true;
                break;
            default:
                matchesRepeat = true;
                break;
        }

        if (!matchesRepeat) {
            return false;
        }

        String timeStr = reminder.getTime();
        try {
            String[] parts = timeStr.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            int currentHour = today.get(java.util.Calendar.HOUR_OF_DAY);
            int currentMinute = today.get(java.util.Calendar.MINUTE);

            return (hour > currentHour) || (hour == currentHour && minute > currentMinute);
        } catch (Exception e) {
            return false;
        }
    }
}
