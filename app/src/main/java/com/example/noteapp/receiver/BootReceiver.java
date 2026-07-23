package com.example.noteapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.example.noteapp.data.AppDatabase;
import com.example.noteapp.model.Task;
import com.example.noteapp.util.TaskScheduler;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;

        if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) return;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            String userIdStr = prefs.getString("user_id", "0");
            int userId;
            try {
                userId = Integer.parseInt(userIdStr);
            } catch (NumberFormatException e) {
                return;
            }

            if (userId == 0) return;

            AppDatabase db = AppDatabase.getInstance(context);
            List<Task> tasks = db.taskDao().getAllSync(userId);

            int notifId = (int) (System.currentTimeMillis());
            for (Task task : tasks) {
                if (task.isDone()) continue;

                if (task.getStartTime() != null && !task.getStartTime().isEmpty()) {
                    TaskScheduler.scheduleTaskReminder(context, notifId++, task.getTitle(), task.getStartTime(), "start");
                }

                if (task.getDeadline() != null && !task.getDeadline().isEmpty()) {
                    TaskScheduler.scheduleTaskReminder(context, notifId++, task.getTitle(), task.getDeadline(), "deadline");
                }
            }
        });
    }
}
