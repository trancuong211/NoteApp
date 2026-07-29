package com.example.noteapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.example.noteapp.util.NotificationHelper;

public class ReminderReceiver extends BroadcastReceiver {

    public static final String EXTRA_REMINDER_ID = "extra_reminder_id";
    public static final String EXTRA_REMINDER_TITLE = "extra_reminder_title";
    public static final String EXTRA_REMINDER_TIME = "extra_reminder_time";
    public static final String EXTRA_REMINDER_REPEAT = "extra_reminder_repeat";

    @Override
    public void onReceive(Context context, Intent intent) {
        int reminderId = intent.getIntExtra(EXTRA_REMINDER_ID, 0);
        String title = intent.getStringExtra(EXTRA_REMINDER_TITLE);
        String time = intent.getStringExtra(EXTRA_REMINDER_TIME);
        String repeat = intent.getStringExtra(EXTRA_REMINDER_REPEAT);

        if (title == null) title = "Nhắc nhở";
        if (time == null) time = "";

        NotificationHelper.showReminder(context, reminderId, title, time, repeat);
    }
}
