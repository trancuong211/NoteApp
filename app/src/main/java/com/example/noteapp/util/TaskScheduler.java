package com.example.noteapp.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import com.example.noteapp.receiver.TaskReminderReceiver;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TaskScheduler {

    public static boolean scheduleTaskReminder(Context context, int taskId, String taskTitle, String dateTime, String type) {
        if (dateTime == null || dateTime.isEmpty()) return false;

        Calendar targetTime = parseDateTime(dateTime);
        if (targetTime == null) return false;

        if ("deadline".equals(type)) {
            SharedPreferences prefs = context.getSharedPreferences("app_prefs", 0);
            int reminderMinutes = prefs.getInt("reminder_default_minutes", 30);
            targetTime.add(Calendar.MINUTE, -reminderMinutes);
        }

        if (targetTime.getTimeInMillis() <= System.currentTimeMillis()) {
            targetTime = Calendar.getInstance();
            targetTime.add(Calendar.SECOND, 10);
        }

        Intent intent = new Intent(context, TaskReminderReceiver.class);
        intent.putExtra(TaskReminderReceiver.EXTRA_TASK_ID, taskId);
        intent.putExtra(TaskReminderReceiver.EXTRA_TASK_TITLE, taskTitle);
        intent.putExtra(TaskReminderReceiver.EXTRA_DATETIME, dateTime);
        intent.putExtra(TaskReminderReceiver.EXTRA_TYPE, type);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, taskId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = context.getSystemService(AlarmManager.class);
        if (alarmManager == null) return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        targetTime.getTimeInMillis(),
                        pendingIntent
                );
                return true;
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                targetTime.getTimeInMillis(),
                pendingIntent
        );
        return true;
    }

    public static void cancelTaskReminder(Context context, int taskId) {
        Intent intent = new Intent(context, TaskReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, taskId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = context.getSystemService(AlarmManager.class);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    public static Calendar parseDateTime(String dateTime) {
        String[] formats = {
                "dd/MM/yyyy HH:mm",
                "yyyy-MM-dd HH:mm",
                "dd/MM/yyyy",
                "yyyy-MM-dd"
        };

        for (String format : formats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
                Date date = sdf.parse(dateTime);
                if (date != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    if (format.equals("dd/MM/yyyy") || format.equals("yyyy-MM-dd")) {
                        cal.set(Calendar.HOUR_OF_DAY, 8);
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.SECOND, 0);
                    }
                    return cal;
                }
            } catch (ParseException ignored) {
            }
        }
        return null;
    }

    public static String getTimeUntil(String dateTime) {
        Calendar target = parseDateTime(dateTime);
        if (target == null) return "";

        long diff = target.getTimeInMillis() - System.currentTimeMillis();
        if (diff < 0) return "Đã quá hạn";

        long hours = diff / (1000 * 60 * 60);
        long minutes = (diff % (1000 * 60 * 60)) / (1000 * 60);

        if (hours > 24) {
            long days = hours / 24;
            return "Còn " + days + " ngày";
        } else if (hours > 0) {
            return "Còn " + hours + "h " + minutes + " phút";
        } else {
            return "Còn " + minutes + " phút";
        }
    }
}
