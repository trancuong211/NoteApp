package com.example.noteapp.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.example.noteapp.receiver.ReminderReceiver;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReminderScheduler {

    public static final int REMINDER_REQUEST_CODE_OFFSET = 50000;

    public static boolean scheduleReminder(Context context, int reminderId, String title, String time, String date, String repeat) {
        if (time == null || time.isEmpty()) return false;

        Calendar targetTime = parseTime(time, date, repeat);
        if (targetTime == null) return false;

        if (targetTime.getTimeInMillis() <= System.currentTimeMillis()) {
            return false;
        }

        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra(ReminderReceiver.EXTRA_REMINDER_ID, reminderId);
        intent.putExtra(ReminderReceiver.EXTRA_REMINDER_TITLE, title);
        intent.putExtra(ReminderReceiver.EXTRA_REMINDER_TIME, time);
        intent.putExtra(ReminderReceiver.EXTRA_REMINDER_REPEAT, repeat);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, reminderId + REMINDER_REQUEST_CODE_OFFSET, intent,
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

    public static void cancelReminder(Context context, int reminderId) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, reminderId + REMINDER_REQUEST_CODE_OFFSET, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = context.getSystemService(AlarmManager.class);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    private static Calendar parseTime(String time, String date, String repeat) {
        String[] timeParts = time.split(":");
        if (timeParts.length != 2) return null;

        int hour, minute;
        try {
            hour = Integer.parseInt(timeParts[0]);
            minute = Integer.parseInt(timeParts[1]);
        } catch (NumberFormatException e) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        List<Integer> targetDays = getDaysOfWeek(repeat);
        if (!targetDays.isEmpty()) {
            int currentDay = cal.get(Calendar.DAY_OF_WEEK);
            int minDaysUntil = Integer.MAX_VALUE;
            for (int targetDay : targetDays) {
                int daysUntil = (targetDay - currentDay + 7) % 7;
                if (daysUntil == 0 && cal.getTimeInMillis() <= System.currentTimeMillis()) {
                    daysUntil = 7;
                }
                if (daysUntil < minDaysUntil) {
                    minDaysUntil = daysUntil;
                }
            }
            cal.add(Calendar.DAY_OF_MONTH, minDaysUntil);
        } else if (date != null && !date.isEmpty()) {
            String[] dateFormats = {"dd/MM/yyyy", "yyyy-MM-dd"};
            for (String fmt : dateFormats) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(fmt, Locale.getDefault());
                    Date d = sdf.parse(date);
                    if (d != null) {
                        cal.setTime(d);
                        cal.set(Calendar.HOUR_OF_DAY, hour);
                        cal.set(Calendar.MINUTE, minute);
                        cal.set(Calendar.SECOND, 0);
                        cal.set(Calendar.MILLISECOND, 0);
                        break;
                    }
                } catch (ParseException ignored) {}
            }
        }

        return cal;
    }

    private static List<Integer> getDaysOfWeek(String repeat) {
        List<Integer> days = new ArrayList<>();
        if (repeat == null) return days;
        String[] parts = repeat.split(",");
        for (String part : parts) {
            String trimmed = part.trim();
            int day = getDayOfWeek(trimmed);
            if (day > 0) {
                days.add(day);
            }
        }
        return days;
    }

    private static int getDayOfWeek(String repeat) {
        if (repeat == null) return -1;
        switch (repeat) {
            case "Thứ 2": return Calendar.MONDAY;
            case "Thứ 3": return Calendar.TUESDAY;
            case "Thứ 4": return Calendar.WEDNESDAY;
            case "Thứ 5": return Calendar.THURSDAY;
            case "Thứ 6": return Calendar.FRIDAY;
            case "Thứ 7": return Calendar.SATURDAY;
            case "Chủ nhật": return Calendar.SUNDAY;
            default: return -1;
        }
    }

    public static void rescheduleForNextWeek(Context context, int reminderId, String title, String time, String repeat) {
        if (time == null || time.isEmpty() || repeat == null) return;

        scheduleReminder(context, reminderId, title, time, null, repeat);
    }
}
