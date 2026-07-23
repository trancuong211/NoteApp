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

    private TextView tvTotalCount;
    private TextView tvActiveCount;
    private TextView tvInactiveCount;
    private TextView tvActiveBadge;
    private TextView tvActiveSectionCount;
    private TextView tvInactiveSectionCount;
    private TextView tvEmptyState;
    private LinearLayout layoutActiveSection;
    private LinearLayout layoutInactiveSection;
    private RecyclerView rvActiveReminders;
    private RecyclerView rvInactiveReminders;

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
        tvEmptyState = view.findViewById(R.id.tv_empty_state);
        layoutActiveSection = view.findViewById(R.id.layout_active_section);
        layoutInactiveSection = view.findViewById(R.id.layout_inactive_section);
        rvActiveReminders = view.findViewById(R.id.rv_active_reminders);
        rvInactiveReminders = view.findViewById(R.id.rv_inactive_reminders);

        rvActiveReminders.setLayoutManager(new LinearLayoutManager(getContext()));
        rvInactiveReminders.setLayoutManager(new LinearLayoutManager(getContext()));

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

        rvActiveReminders.setAdapter(activeAdapter);
        rvInactiveReminders.setAdapter(inactiveAdapter);

        reminderViewModel.getReminders().observe(getViewLifecycleOwner(), reminders -> {
            List<Reminder> activeList = new ArrayList<>();
            List<Reminder> inactiveList = new ArrayList<>();

            if (reminders != null) {
                for (Reminder r : reminders) {
                    if (r.isActive()) {
                        activeList.add(r);
                    } else {
                        inactiveList.add(r);
                    }
                }
            }

            int totalCount = reminders != null ? reminders.size() : 0;
            int activeCount = activeList.size();
            int inactiveCount = inactiveList.size();

            tvTotalCount.setText(String.valueOf(totalCount));
            tvActiveCount.setText(String.valueOf(activeCount));
            tvInactiveCount.setText(String.valueOf(inactiveCount));
            tvActiveBadge.setText(activeCount + " đang bật");
            tvActiveSectionCount.setText(String.valueOf(activeCount));
            tvInactiveSectionCount.setText(String.valueOf(inactiveCount));

            activeAdapter.setReminders(activeList);
            inactiveAdapter.setReminders(inactiveList);

            if (totalCount == 0) {
                tvEmptyState.setVisibility(View.VISIBLE);
                layoutActiveSection.setVisibility(View.GONE);
                rvActiveReminders.setVisibility(View.GONE);
                layoutInactiveSection.setVisibility(View.GONE);
                rvInactiveReminders.setVisibility(View.GONE);
            } else {
                tvEmptyState.setVisibility(View.GONE);

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
}
