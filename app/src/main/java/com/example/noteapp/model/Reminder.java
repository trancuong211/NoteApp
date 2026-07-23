package com.example.noteapp.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "reminders")
public class Reminder {
    @PrimaryKey
    private int id;
    private String title;
    private String time;
    private String date;
    private boolean active;
    private String repeat;
    private String icon;
    private String color;
    private int userId;

    public Reminder() {
        this.id = 0;
        this.title = "";
        this.time = "";
        this.date = "";
        this.active = true;
        this.repeat = "";
        this.icon = "";
        this.color = "";
    }

    @Ignore
    public Reminder(int id, String title, String time, String date, boolean active, String repeat, String icon, String color) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.date = date;
        this.active = active;
        this.repeat = repeat;
        this.icon = icon;
        this.color = color;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getRepeat() { return repeat; }
    public void setRepeat(String repeat) { this.repeat = repeat; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}
