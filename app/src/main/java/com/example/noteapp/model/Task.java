package com.example.noteapp.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public class Task {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String category;
    private String priority;
    private String status;
    private String startTime;
    private String deadline;
    private String dateKey;
    private int userId;

    public Task() {
        this.title = "";
        this.category = "";
        this.priority = "";
        this.status = "todo";
        this.startTime = "";
        this.deadline = "";
        this.dateKey = "";
    }

    @Ignore
    public Task(String title, String category, String priority) {
        this.title = title;
        this.category = category;
        this.priority = priority;
        this.status = "todo";
        this.startTime = "";
        this.deadline = "";
        this.dateKey = "";
    }

    @Ignore
    public Task(String title, String category, String priority, String status, String startTime, String deadline, String dateKey) {
        this.title = title;
        this.category = category;
        this.priority = priority;
        this.status = status;
        this.startTime = startTime;
        this.deadline = deadline;
        this.dateKey = dateKey;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public String getDateKey() { return dateKey; }
    public void setDateKey(String dateKey) { this.dateKey = dateKey; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public boolean isDone() { return "done".equals(status); }
    public void setDone(boolean done) { status = done ? "done" : "todo"; }
}
