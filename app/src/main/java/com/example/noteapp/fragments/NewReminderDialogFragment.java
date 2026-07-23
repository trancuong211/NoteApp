package com.example.noteapp.fragments;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.noteapp.R;
import com.example.noteapp.model.Reminder;
import com.example.noteapp.util.UserManager;
import com.example.noteapp.viewmodel.ReminderViewModel;
import com.google.android.material.chip.ChipGroup;

public class NewReminderDialogFragment extends DialogFragment {

    private static final String[] REMINDER_ICONS = {"💧", "📅", "💪", "📖", "📧", "🏃", "💊", "🍎", "☕", "🛌"};
    private static final String[] REMINDER_COLORS = {"#38BDF8", "#00D68F", "#7C3AED", "#F59E0B", "#FF6B6B", "#EC4899"};
    private static final String[] REPEAT_OPTIONS = {"Một lần", "Hàng ngày", "Ngày làm việc", "Cuối tuần", "Hàng tuần", "Hàng tháng"};
    private static final String[] REPEAT_DATES = {"Một lần", "Hàng ngày", "Thứ 2 - Thứ 6", "Cuối tuần", "Hàng tuần", "Hàng tháng"};

    private String selectedIcon = "💧";
    private String selectedColor = "#38BDF8";
    private String selectedRepeat = "Hàng ngày";
    private ReminderViewModel reminderViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_new_reminder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        reminderViewModel = new ViewModelProvider(requireActivity()).get(ReminderViewModel.class);

        ImageView btnClose = view.findViewById(R.id.btn_close);
        EditText etTitle = view.findViewById(R.id.et_reminder_title);
        EditText etTime = view.findViewById(R.id.et_time);
        TextView btnAdd = view.findViewById(R.id.btn_add_reminder);
        ChipGroup chipGroupRepeat = view.findViewById(R.id.chip_group_repeat);

        btnClose.setOnClickListener(v -> dismiss());

        // Setup icon grid
        setupIconGrid(view);

        // Setup color picker
        setupColorPicker(view);

        // Setup repeat chips
        setupRepeatChips(chipGroupRepeat);

        // Add button
        btnAdd.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            if (title.isEmpty()) {
                etTitle.setError("Vui lòng nhập tên nhắc nhở");
                return;
            }
            String time = etTime.getText().toString().trim();
            if (time.isEmpty()) time = "08:00";

            int repeatIndex = getSelectedRepeatIndex(chipGroupRepeat);
            String repeat = REPEAT_OPTIONS[repeatIndex];
            String date = REPEAT_DATES[repeatIndex];

            Reminder reminder = new Reminder(
                (int) System.currentTimeMillis(),
                title,
                time,
                date,
                true,
                repeat,
                selectedIcon,
                selectedColor
            );
            reminder.setUserId(UserManager.getUserId(requireContext()));
            reminderViewModel.insert(reminder);
            dismiss();
        });
    }

    private void setupIconGrid(View view) {
        int[] iconIds = {
            R.id.icon_0, R.id.icon_1, R.id.icon_2, R.id.icon_3, R.id.icon_4,
            R.id.icon_5, R.id.icon_6, R.id.icon_7, R.id.icon_8, R.id.icon_9
        };

        for (int i = 0; i < iconIds.length; i++) {
            TextView iconView = view.findViewById(iconIds[i]);
            final int index = i;
            iconView.setOnClickListener(v -> {
                selectedIcon = REMINDER_ICONS[index];
                updateIconSelection(view, iconIds, index);
            });
        }

        // Select first icon
        if (view.findViewById(iconIds[0]) != null) {
            view.findViewById(iconIds[0]).setBackgroundResource(R.drawable.bg_chip_category_selected);
        }
    }

    private void updateIconSelection(View view, int[] iconIds, int selectedIndex) {
        for (int i = 0; i < iconIds.length; i++) {
            View iconView = view.findViewById(iconIds[i]);
            if (iconView != null) {
                if (i == selectedIndex) {
                    iconView.setBackgroundResource(R.drawable.bg_chip_category_selected);
                } else {
                    iconView.setBackgroundResource(R.drawable.bg_chip_category_default);
                }
            }
        }
    }

    private void setupColorPicker(View view) {
        int[] colorIds = {
            R.id.color_0, R.id.color_1, R.id.color_2,
            R.id.color_3, R.id.color_4, R.id.color_5
        };

        for (int i = 0; i < colorIds.length; i++) {
            View colorView = view.findViewById(colorIds[i]);
            final int index = i;
            colorView.setOnClickListener(v -> {
                selectedColor = REMINDER_COLORS[index];
                updateColorSelection(view, colorIds, index);
            });
        }

        // Select first color
        updateColorSelection(view, colorIds, 0);
    }

    private void updateColorSelection(View view, int[] colorIds, int selectedIndex) {
        for (int i = 0; i < colorIds.length; i++) {
            View colorView = view.findViewById(colorIds[i]);
            if (colorView != null) {
                if (i == selectedIndex) {
                    colorView.animate().scaleX(1.3f).scaleY(1.3f).setDuration(150).start();
                } else {
                    colorView.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
                }
            }
        }
    }

    private void setupRepeatChips(ChipGroup chipGroup) {
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            // Default to daily if nothing selected
        });
    }

    private int getSelectedRepeatIndex(ChipGroup chipGroup) {
        int checkedId = chipGroup.getCheckedChipId();
        if (checkedId == R.id.chip_once) return 0;
        if (checkedId == R.id.chip_daily) return 1;
        if (checkedId == R.id.chip_workdays) return 2;
        if (checkedId == R.id.chip_weekend) return 3;
        if (checkedId == R.id.chip_weekly) return 4;
        if (checkedId == R.id.chip_monthly) return 5;
        return 1; // default: hàng ngày
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
