package com.example.noteapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.example.noteapp.util.NotificationHelper;

public class TaskReminderReceiver extends BroadcastReceiver {

    public static final String EXTRA_TASK_ID = "extra_task_id";
    public static final String EXTRA_TASK_TITLE = "extra_task_title";
    public static final String EXTRA_DATETIME = "extra_datetime";
    public static final String EXTRA_TYPE = "extra_type";

    @Override
    public void onReceive(Context context, Intent intent) {
        int taskId = intent.getIntExtra(EXTRA_TASK_ID, 0);
        String taskTitle = intent.getStringExtra(EXTRA_TASK_TITLE);
        String dateTime = intent.getStringExtra(EXTRA_DATETIME);
        String type = intent.getStringExtra(EXTRA_TYPE);

        if (taskTitle == null) taskTitle = "Nhiệm vụ mới";
        if (type == null) type = "deadline";

        NotificationHelper.showTaskReminder(context, taskId, taskTitle, dateTime, type);
    }
}
