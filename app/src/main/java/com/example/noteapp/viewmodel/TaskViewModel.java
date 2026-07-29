package com.example.noteapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.noteapp.data.AppDatabase;
import com.example.noteapp.data.TaskDao;
import com.example.noteapp.model.Task;
import com.example.noteapp.util.TaskScheduler;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskViewModel extends AndroidViewModel {

    private final TaskDao taskDao;
    private final ExecutorService executor;
    private int userId;
    private LiveData<List<Task>> allTasks;
    private LiveData<Integer> totalCount;
    private LiveData<Integer> doneCount;
    private LiveData<Integer> pendingCount;
    private LiveData<Integer> inProgressCount;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        taskDao = db.taskDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public void setUserId(int userId) {
        this.userId = userId;
        allTasks = taskDao.getAll(userId);
        totalCount = taskDao.getCount(userId);
        doneCount = taskDao.getDoneCount(userId);
        pendingCount = taskDao.getPendingCount(userId);
        inProgressCount = taskDao.getInProgressCount(userId);
    }

    public LiveData<List<Task>> getTasks() { return allTasks; }
    public LiveData<Integer> getTotalCount() { return totalCount; }
    public LiveData<Integer> getDoneCount() { return doneCount; }
    public LiveData<Integer> getPendingCount() { return pendingCount; }
    public LiveData<Integer> getInProgressCount() { return inProgressCount; }

    public LiveData<List<Task>> getTasksForDate(String dateKey) {
        return taskDao.getByDate(userId, dateKey);
    }

    public void insert(Task task) {
        executor.execute(() -> taskDao.insert(task));
    }

    public void update(Task task) {
        executor.execute(() -> taskDao.update(task));
    }

    public void delete(Task task) {
        executor.execute(() -> {
            if (task.getTitle() != null) {
                int notifIdBase = Math.abs(task.getTitle().hashCode());
                TaskScheduler.cancelTaskReminder(getApplication(), notifIdBase);
                TaskScheduler.cancelTaskReminder(getApplication(), notifIdBase + 1);
            }
            taskDao.delete(task);
        });
    }

    public void deleteById(int id) {
        executor.execute(() -> {
            TaskScheduler.cancelTaskReminder(getApplication(), id);
            TaskScheduler.cancelTaskReminder(getApplication(), id + 1);
            taskDao.deleteById(id);
        });
    }

    public void toggleTaskDone(Task task) {
        executor.execute(() -> {
            String newStatus = "done".equals(task.getStatus()) ? "todo" : "done";
            task.setStatus(newStatus);
            taskDao.update(task);
        });
    }

    public void setTaskStatus(Task task, String status) {
        executor.execute(() -> {
            task.setStatus(status);
            taskDao.update(task);
        });
    }
}
