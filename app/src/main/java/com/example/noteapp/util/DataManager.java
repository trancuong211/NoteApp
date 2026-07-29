package com.example.noteapp.util;

import android.content.Context;
import android.os.Environment;
import com.example.noteapp.data.AppDatabase;
import com.example.noteapp.model.Reminder;
import com.example.noteapp.model.Task;
import com.example.noteapp.model.User;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataManager {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public interface OnResultListener {
        void onSuccess(String message);
        void onError(String error);
    }

    public static void exportData(Context context, OnResultListener listener) {
        executor.execute(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(context);
                List<User> users = db.userDao().getAllUsers();

                JSONObject root = new JSONObject();
                JSONArray usersArray = new JSONArray();

                for (User user : users) {
                    JSONObject userObj = new JSONObject();
                    userObj.put("id", user.getId());
                    userObj.put("fullName", user.getFullName());
                    userObj.put("email", user.getEmail());
                    userObj.put("phone", user.getPhone());
                    userObj.put("password", user.getPassword());

                    List<Task> tasks = db.taskDao().getAllSync(user.getId());
                    JSONArray tasksArray = new JSONArray();
                    for (Task task : tasks) {
                        JSONObject taskObj = new JSONObject();
                        taskObj.put("id", task.getId());
                        taskObj.put("title", task.getTitle());
                        taskObj.put("category", task.getCategory());
                        taskObj.put("priority", task.getPriority());
                        taskObj.put("status", task.getStatus());
                        taskObj.put("startTime", task.getStartTime());
                        taskObj.put("deadline", task.getDeadline());
                        taskObj.put("dateKey", task.getDateKey());
                        taskObj.put("userId", task.getUserId());
                        tasksArray.put(taskObj);
                    }
                    userObj.put("tasks", tasksArray);

                    List<Reminder> reminders = db.reminderDao().getAllSync(user.getId());
                    JSONArray remindersArray = new JSONArray();
                    for (Reminder reminder : reminders) {
                        JSONObject reminderObj = new JSONObject();
                        reminderObj.put("id", reminder.getId());
                        reminderObj.put("title", reminder.getTitle());
                        reminderObj.put("time", reminder.getTime());
                        reminderObj.put("date", reminder.getDate());
                        reminderObj.put("active", reminder.isActive());
                        reminderObj.put("repeat", reminder.getRepeat());
                        reminderObj.put("icon", reminder.getIcon());
                        reminderObj.put("color", reminder.getColor());
                        reminderObj.put("userId", reminder.getUserId());
                        remindersArray.put(reminderObj);
                    }
                    userObj.put("reminders", remindersArray);

                    usersArray.put(userObj);
                }

                root.put("users", usersArray);
                root.put("exportDate", System.currentTimeMillis());
                root.put("version", 1);

                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                if (!dir.exists()) dir.mkdirs();

                File file = new File(dir, "noteapp_backup.json");
                FileWriter writer = new FileWriter(file);
                writer.write(root.toString(2));
                writer.close();

                if (listener != null) {
                    listener.onSuccess("Đã lưu vào: " + file.getAbsolutePath());
                }
            } catch (Exception e) {
                if (listener != null) {
                    listener.onError("Lỗi xuất dữ liệu: " + e.getMessage());
                }
            }
        });
    }

    public static void importData(Context context, OnResultListener listener) {
        executor.execute(() -> {
            try {
                File file = new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                        "noteapp_backup.json"
                );

                if (!file.exists()) {
                    if (listener != null) {
                        listener.onError("Không tìm thấy file backup: " + file.getAbsolutePath());
                    }
                    return;
                }

                FileReader reader = new FileReader(file);
                StringBuilder sb = new StringBuilder();
                int c;
                while ((c = reader.read()) != -1) {
                    sb.append((char) c);
                }
                reader.close();

                JSONObject root = new JSONObject(sb.toString());
                JSONArray usersArray = root.getJSONArray("users");

                AppDatabase db = AppDatabase.getInstance(context);
                int importedCount = 0;

                for (int i = 0; i < usersArray.length(); i++) {
                    JSONObject userObj = usersArray.getJSONObject(i);

                    String email = userObj.getString("email");
                    User existingUser = db.userDao().getByEmail(email);

                    int userIdInt;
                    if (existingUser != null) {
                        userIdInt = existingUser.getId();
                    } else {
                        User user = new User(
                                userObj.getString("fullName"),
                                email,
                                userObj.getString("phone"),
                                userObj.getString("password")
                        );
                        long userId = db.userDao().insert(user);
                        userIdInt = (int) userId;
                        importedCount++;
                    }

                    JSONArray tasksArray = userObj.getJSONArray("tasks");
                    for (int j = 0; j < tasksArray.length(); j++) {
                        JSONObject taskObj = tasksArray.getJSONObject(j);
                        Task task = new Task();
                        task.setTitle(taskObj.getString("title"));
                        task.setCategory(taskObj.getString("category"));
                        task.setPriority(taskObj.getString("priority"));
                        task.setStatus(taskObj.getString("status"));
                        task.setStartTime(taskObj.optString("startTime", ""));
                        task.setDeadline(taskObj.getString("deadline"));
                        task.setDateKey(taskObj.getString("dateKey"));
                        task.setUserId(userIdInt);
                        db.taskDao().insert(task);
                    }

                    JSONArray remindersArray = userObj.getJSONArray("reminders");
                    for (int j = 0; j < remindersArray.length(); j++) {
                        JSONObject reminderObj = remindersArray.getJSONObject(j);
                        Reminder reminder = new Reminder(
                                reminderObj.getInt("id"),
                                reminderObj.getString("title"),
                                reminderObj.getString("time"),
                                reminderObj.getString("date"),
                                reminderObj.getBoolean("active"),
                                reminderObj.getString("repeat"),
                                reminderObj.getString("icon"),
                                reminderObj.getString("color")
                        );
                        reminder.setUserId(userIdInt);
                        db.reminderDao().insert(reminder);
                    }
                }

                if (listener != null) {
                    listener.onSuccess("Đã nhập dữ liệu từ " + importedCount + " tài khoản mới!");
                }
            } catch (Exception e) {
                if (listener != null) {
                    listener.onError("Lỗi nhập dữ liệu: " + e.getMessage());
                }
            }
        });
    }
}
