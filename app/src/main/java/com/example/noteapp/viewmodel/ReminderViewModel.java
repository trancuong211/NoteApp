package com.example.noteapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.noteapp.data.AppDatabase;
import com.example.noteapp.data.ReminderDao;
import com.example.noteapp.model.Reminder;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReminderViewModel extends AndroidViewModel {

    private final ReminderDao reminderDao;
    private final ExecutorService executor;
    private int userId;
    private LiveData<List<Reminder>> allReminders;
    private LiveData<List<Reminder>> activeReminders;
    private LiveData<List<Reminder>> inactiveReminders;
    private LiveData<Integer> totalCount;
    private LiveData<Integer> activeCount;
    private LiveData<Integer> inactiveCount;

    public ReminderViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        reminderDao = db.reminderDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public void setUserId(int userId) {
        this.userId = userId;
        allReminders = reminderDao.getAll(userId);
        activeReminders = reminderDao.getActive(userId);
        inactiveReminders = reminderDao.getInactive(userId);
        totalCount = reminderDao.getCount(userId);
        activeCount = reminderDao.getActiveCount(userId);
        inactiveCount = reminderDao.getInactiveCount(userId);
    }

    public LiveData<List<Reminder>> getReminders() { return allReminders; }
    public LiveData<List<Reminder>> getActiveReminders() { return activeReminders; }
    public LiveData<List<Reminder>> getInactiveReminders() { return inactiveReminders; }
    public LiveData<Integer> getTotalCount() { return totalCount; }
    public LiveData<Integer> getActiveCount() { return activeCount; }
    public LiveData<Integer> getInactiveCount() { return inactiveCount; }

    public void insert(Reminder reminder) {
        executor.execute(() -> reminderDao.insert(reminder));
    }

    public void update(Reminder reminder) {
        executor.execute(() -> reminderDao.update(reminder));
    }

    public void delete(Reminder reminder) {
        executor.execute(() -> reminderDao.delete(reminder));
    }

    public void deleteById(int id) {
        executor.execute(() -> reminderDao.deleteById(id));
    }

    public void toggleReminder(Reminder reminder) {
        executor.execute(() -> {
            reminder.setActive(!reminder.isActive());
            reminderDao.update(reminder);
        });
    }
}
