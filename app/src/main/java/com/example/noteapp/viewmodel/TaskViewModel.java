package com.example.noteapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.noteapp.model.Task;
import java.util.ArrayList;
import java.util.List;

public class TaskViewModel extends ViewModel {
    private final MutableLiveData<List<Task>> tasks = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<Task>> getTasks() {
        return tasks;
    }

    public void addTask(Task task) {
        List<Task> currentTasks = tasks.getValue();
        if (currentTasks != null) {
            currentTasks.add(0, task);
            tasks.setValue(currentTasks);
        }
    }

    public void removeTask(int position) {
        List<Task> currentTasks = tasks.getValue();
        if (currentTasks != null && position >= 0 && position < currentTasks.size()) {
            currentTasks.remove(position);
            tasks.setValue(currentTasks);
        }
    }

    public void toggleTaskDone(int position) {
        List<Task> currentTasks = tasks.getValue();
        if (currentTasks != null && position >= 0 && position < currentTasks.size()) {
            Task task = currentTasks.get(position);
            task.setDone(!task.isDone());
            tasks.setValue(currentTasks);
        }
    }

    public int getPendingCount() {
        List<Task> currentTasks = tasks.getValue();
        if (currentTasks == null) return 0;
        int count = 0;
        for (Task task : currentTasks) {
            if (!task.isDone()) count++;
        }
        return count;
    }

    public int getDoneCount() {
        List<Task> currentTasks = tasks.getValue();
        if (currentTasks == null) return 0;
        int count = 0;
        for (Task task : currentTasks) {
            if (task.isDone()) count++;
        }
        return count;
    }
}
