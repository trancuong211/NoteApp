package com.example.noteapp.model;

public class Task {
    private String title;
    private String category;
    private String priority;
    private boolean isDone;

    public Task(String title, String category, String priority) {
        this.title = title;
        this.category = category;
        this.priority = priority;
        this.isDone = false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
