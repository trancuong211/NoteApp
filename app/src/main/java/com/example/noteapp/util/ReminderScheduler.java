package com.example.noteapp.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.example.noteapp.receiver.ReminderReceiver;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReminderScheduler {

    public static boolean scheduleReminder(Context context, int reminderId, String title, String time, String date, String repeat) {
        if (time == null || time.isEmpty()) return false;

        Calendar targetTime = parseTime(time, date);
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
                context, reminderId + 50000, intent,
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
                context, reminderId + 50000, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = context.getSystemService(AlarmManager.class);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    private static Calendar parseTime(String time, String date) {
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

        if (date != null && !date.isEmpty()) {
            String[] dateFormats = {"dd/MM/yyyy", "yyyy-MM-dd"};
            for (String fmt : dateFormats) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(fmt, Locale.getDefault());
                    Date d = sdf.parse(date);
                    if (d != null) {
                        cal.setTime(d);
                        break;
                    }
                } catch (ParseException ignored) {}
            }
        }

        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal;
    }
}
