package com.example.noteapp.fragments;

import android.app.TimePickerDialog;
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
import com.example.noteapp.util.ReminderScheduler;
import com.example.noteapp.util.UserManager;
import com.example.noteapp.viewmodel.ReminderViewModel;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NewReminderDialogFragment extends DialogFragment {

    private static final String[] REMINDER_ICONS = {"💧", "📅", "💪", "📖", "📧", "🏃", "💊", "🍎", "☕", "🛌"};
    private static final String[] REMINDER_COLORS = {"#38BDF8", "#00D68F", "#7C3AED", "#F59E0B", "#FF6B6B", "#EC4899"};
    private static final String[] REPEAT_OPTIONS = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"};
    private static final int[] REPEAT_DAYS = {Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY};

    private String selectedIcon = "💧";
    private String selectedColor = "#38BDF8";
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

        etTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(requireContext(), (timeView, hourOfDay, minute) -> {
                String time = String.format(java.util.Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                etTime.setText(time);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });

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
            if (time.isEmpty()) {
                time = "08:00";
            } else if (!time.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
                etTime.setError("Định dạng giờ không hợp lệ (HH:mm)");
                return;
            }

            int repeatIndex = getSelectedRepeatIndex(chipGroupRepeat);
            String repeat = buildRepeatString(chipGroupRepeat);
            String date = repeat;

            Reminder reminder = new Reminder(
                0,
                title,
                time,
                date,
                true,
                repeat,
                selectedIcon,
                selectedColor
            );
            reminder.setUserId(UserManager.getUserId(requireContext()));
            reminderViewModel.insertWithCallback(reminder, id -> {
                ReminderScheduler.scheduleReminder(
                    requireContext(),
                    (int) id,
                    title,
                    time,
                    date,
                    repeat
                );
            });

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
        if (checkedId == R.id.chip_mon) return 0;
        if (checkedId == R.id.chip_tue) return 1;
        if (checkedId == R.id.chip_wed) return 2;
        if (checkedId == R.id.chip_thu) return 3;
        if (checkedId == R.id.chip_fri) return 4;
        if (checkedId == R.id.chip_sat) return 5;
        if (checkedId == R.id.chip_sun) return 6;
        return 0; // default: Thứ 2
    }

    private List<Integer> getSelectedRepeatDays(ChipGroup chipGroup) {
        List<Integer> selectedDays = new ArrayList<>();
        int[] chipIds = {R.id.chip_mon, R.id.chip_tue, R.id.chip_wed, R.id.chip_thu, R.id.chip_fri, R.id.chip_sat, R.id.chip_sun};
        for (int i = 0; i < chipIds.length; i++) {
            com.google.android.material.chip.Chip chip = chipGroup.findViewById(chipIds[i]);
            if (chip != null && chip.isChecked()) {
                selectedDays.add(i);
            }
        }
        if (selectedDays.isEmpty()) {
            selectedDays.add(0);
        }
        return selectedDays;
    }

    private String buildRepeatString(ChipGroup chipGroup) {
        List<Integer> days = getSelectedRepeatDays(chipGroup);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < days.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(REPEAT_OPTIONS[days.get(i)]);
        }
        return sb.toString();
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
