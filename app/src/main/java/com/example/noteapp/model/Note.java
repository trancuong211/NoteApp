package com.example.noteapp.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes")
public class Note {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String content;
    private String category;
    private long createdAt;
    private int userId;

    public Note() {
        this.title = "";
        this.content = "";
        this.category = "";
        this.createdAt = System.currentTimeMillis();
    }

    @Ignore
    public Note(String title, String content, String category) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.createdAt = System.currentTimeMillis();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}
