package com.example.noteapp.fragments;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.noteapp.R;
import com.example.noteapp.model.Task;
import com.example.noteapp.util.UserManager;
import com.example.noteapp.view.PieChartView;
import com.example.noteapp.viewmodel.TaskViewModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class StatsFragment extends Fragment {

    private TaskViewModel taskViewModel;

    private TextView tvTotalTasks;
    private TextView tvCompleted;
    private TextView tvCompletionRate;
    private TextView tvDone;
    private TextView tvRemaining;
    private ProgressBar progressCompletion;

    private LinearLayout barMon, barTue, barWed, barThu, barFri, barSat, barSun;
    private TextView tvCountMon, tvCountTue, tvCountWed, tvCountThu, tvCountFri, tvCountSat, tvCountSun;
    private View barViewMon, barViewTue, barViewWed, barViewThu, barViewFri, barViewSat, barViewSun;

    private TextView tvWorkCount, tvPersonalCount, tvStudyCount, tvHealthCount;
    private TextView tvHighCount, tvMediumCount, tvLowCount;

    private PieChartView pieChartCategory;
    private PieChartView pieChartPriority;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
        int userId = UserManager.getUserId(requireContext());
        taskViewModel.setUserId(userId);

        initViews(view);
        setupBarChartClickListeners();
        observeData();
    }

    private void initViews(View view) {
        tvTotalTasks = view.findViewById(R.id.tv_total_tasks);
        tvCompleted = view.findViewById(R.id.tv_completed);
        tvCompletionRate = view.findViewById(R.id.tv_completion_rate);
        tvDone = view.findViewById(R.id.tv_done);
        tvRemaining = view.findViewById(R.id.tv_remaining);
        progressCompletion = view.findViewById(R.id.progress_completion);

        barMon = view.findViewById(R.id.bar_mon);
        barTue = view.findViewById(R.id.bar_tue);
        barWed = view.findViewById(R.id.bar_wed);
        barThu = view.findViewById(R.id.bar_thu);
        barFri = view.findViewById(R.id.bar_fri);
        barSat = view.findViewById(R.id.bar_sat);
        barSun = view.findViewById(R.id.bar_sun);

        tvCountMon = view.findViewById(R.id.tv_count_mon);
        tvCountTue = view.findViewById(R.id.tv_count_tue);
        tvCountWed = view.findViewById(R.id.tv_count_wed);
        tvCountThu = view.findViewById(R.id.tv_count_thu);
        tvCountFri = view.findViewById(R.id.tv_count_fri);
        tvCountSat = view.findViewById(R.id.tv_count_sat);
        tvCountSun = view.findViewById(R.id.tv_count_sun);

        barViewMon = view.findViewById(R.id.bar_view_mon);
        barViewTue = view.findViewById(R.id.bar_view_tue);
        barViewWed = view.findViewById(R.id.bar_view_wed);
        barViewThu = view.findViewById(R.id.bar_view_thu);
        barViewFri = view.findViewById(R.id.bar_view_fri);
        barViewSat = view.findViewById(R.id.bar_view_sat);
        barViewSun = view.findViewById(R.id.bar_view_sun);

        tvWorkCount = view.findViewById(R.id.tv_work_count);
        tvPersonalCount = view.findViewById(R.id.tv_personal_count);
        tvStudyCount = view.findViewById(R.id.tv_study_count);
        tvHealthCount = view.findViewById(R.id.tv_health_count);

        tvHighCount = view.findViewById(R.id.tv_high_count);
        tvMediumCount = view.findViewById(R.id.tv_medium_count);
        tvLowCount = view.findViewById(R.id.tv_low_count);

        pieChartCategory = view.findViewById(R.id.pie_chart_category);
        pieChartPriority = view.findViewById(R.id.pie_chart_priority);
    }

    private void setupBarChartClickListeners() {
        View.OnClickListener barClickListener = v -> {
            resetAllBars();
            int id = v.getId();
            TextView countText = null;
            View barView = null;

            if (id == R.id.bar_mon) {
                countText = tvCountMon;
                barView = barViewMon;
            } else if (id == R.id.bar_tue) {
                countText = tvCountTue;
                barView = barViewTue;
            } else if (id == R.id.bar_wed) {
                countText = tvCountWed;
                barView = barViewWed;
            } else if (id == R.id.bar_thu) {
                countText = tvCountThu;
                barView = barViewThu;
            } else if (id == R.id.bar_fri) {
                countText = tvCountFri;
                barView = barViewFri;
            } else if (id == R.id.bar_sat) {
                countText = tvCountSat;
                barView = barViewSat;
            } else if (id == R.id.bar_sun) {
                countText = tvCountSun;
                barView = barViewSun;
            }

            if (countText != null && barView != null) {
                countText.setVisibility(View.VISIBLE);
                GradientDrawable bg = (GradientDrawable) barView.getBackground();
                bg.setColor(Color.parseColor("#00E676"));
                barView.setBackground(bg);
            }
        };

        barMon.setOnClickListener(barClickListener);
        barTue.setOnClickListener(barClickListener);
        barWed.setOnClickListener(barClickListener);
        barThu.setOnClickListener(barClickListener);
        barFri.setOnClickListener(barClickListener);
        barSat.setOnClickListener(barClickListener);
        barSun.setOnClickListener(barClickListener);
    }

    private void resetAllBars() {
        TextView[] countTexts = {tvCountMon, tvCountTue, tvCountWed, tvCountThu, tvCountFri, tvCountSat, tvCountSun};
        View[] barViews = {barViewMon, barViewTue, barViewWed, barViewThu, barViewFri, barViewSat, barViewSun};

        for (int i = 0; i < countTexts.length; i++) {
            countTexts[i].setVisibility(View.GONE);
            GradientDrawable bg = (GradientDrawable) barViews[i].getBackground();
            bg.setColor(Color.parseColor("#1A3D2A"));
            barViews[i].setBackground(bg);
        }
    }

    private void observeData() {
        taskViewModel.getTasks().observe(getViewLifecycleOwner(), tasks -> {
            updateSummaryCards(tasks);
            updateWeeklyActivity(tasks);
            updateCategoryStats(tasks);
            updatePriorityStats(tasks);
            updateCompletionRate(tasks);
            updatePieCharts(tasks);
        });
    }

    private void updateSummaryCards(List<Task> tasks) {
        int total = tasks != null ? tasks.size() : 0;
        int completed = 0;
        if (tasks != null) {
            for (Task task : tasks) {
                if (task.isDone()) completed++;
            }
        }

        tvTotalTasks.setText(String.valueOf(total));
        tvCompleted.setText(String.valueOf(completed));
    }

    private void updateWeeklyActivity(List<Task> tasks) {
        if (tasks == null) return;

        int[] dailyDone = new int[7];
        Calendar cal = Calendar.getInstance();
        int today = cal.get(Calendar.DAY_OF_WEEK);

        for (Task task : tasks) {
            if (task.isDone()) {
                dailyDone[today - 1]++;
            }
        }

        int maxCount = 1;
        for (int count : dailyDone) {
            if (count > maxCount) maxCount = count;
        }

        TextView[] countTexts = {tvCountMon, tvCountTue, tvCountWed, tvCountThu, tvCountFri, tvCountSat, tvCountSun};
        View[] barViews = {barViewMon, barViewTue, barViewWed, barViewThu, barViewFri, barViewSat, barViewSun};

        for (int i = 0; i < 7; i++) {
            countTexts[i].setText(dailyDone[i] + " done");
            int height = (int) (40 + (dailyDone[i] * 100.0 / maxCount));
            ViewGroup.LayoutParams params = barViews[i].getLayoutParams();
            params.height = height;
            barViews[i].setLayoutParams(params);
        }
    }

    private void updateCategoryStats(List<Task> tasks) {
        if (tasks == null) return;

        int work = 0, personal = 0, study = 0, health = 0;
        for (Task task : tasks) {
            switch (task.getCategory()) {
                case "work": work++; break;
                case "personal": personal++; break;
                case "study": study++; break;
                case "health": health++; break;
            }
        }

        tvWorkCount.setText(String.valueOf(work));
        tvPersonalCount.setText(String.valueOf(personal));
        tvStudyCount.setText(String.valueOf(study));
        tvHealthCount.setText(String.valueOf(health));
    }

    private void updatePriorityStats(List<Task> tasks) {
        if (tasks == null) return;

        int high = 0, medium = 0, low = 0;
        for (Task task : tasks) {
            switch (task.getPriority()) {
                case "high": high++; break;
                case "medium": medium++; break;
                case "low": low++; break;
            }
        }

        tvHighCount.setText(String.valueOf(high));
        tvMediumCount.setText(String.valueOf(medium));
        tvLowCount.setText(String.valueOf(low));
    }

    private void updateCompletionRate(List<Task> tasks) {
        int total = tasks != null ? tasks.size() : 0;
        int done = 0;
        if (tasks != null) {
            for (Task task : tasks) {
                if (task.isDone()) done++;
            }
        }
        int remaining = total - done;

        int percent = total > 0 ? (done * 100 / total) : 0;

        tvCompletionRate.setText(percent + "%");
        tvDone.setText(done + " done");
        tvRemaining.setText(remaining + " remaining");
        progressCompletion.setProgress(percent);
    }

    private void updatePieCharts(List<Task> tasks) {
        if (tasks == null) return;

        int work = 0, personal = 0, study = 0, health = 0;
        for (Task task : tasks) {
            switch (task.getCategory()) {
                case "work": work++; break;
                case "personal": personal++; break;
                case "study": study++; break;
                case "health": health++; break;
            }
        }

        List<Float> categoryValues = Arrays.asList(
                (float) work, (float) personal, (float) study, (float) health
        );
        List<Integer> categoryColors = Arrays.asList(
                Color.parseColor("#00E676"),
                Color.parseColor("#B388FF"),
                Color.parseColor("#448AFF"),
                Color.parseColor("#FF5252")
        );
        pieChartCategory.setData(categoryValues, categoryColors);

        int high = 0, medium = 0, low = 0;
        for (Task task : tasks) {
            switch (task.getPriority()) {
                case "high": high++; break;
                case "medium": medium++; break;
                case "low": low++; break;
            }
        }

        List<Float> priorityValues = Arrays.asList(
                (float) high, (float) medium, (float) low
        );
        List<Integer> priorityColors = Arrays.asList(
                Color.parseColor("#FF5252"),
                Color.parseColor("#FF9800"),
                Color.parseColor("#00E676")
        );
        pieChartPriority.setData(priorityValues, priorityColors);
    }
}
