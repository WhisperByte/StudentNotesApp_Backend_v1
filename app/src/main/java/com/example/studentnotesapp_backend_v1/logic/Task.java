package com.example.studentnotesapp_backend_v1.logic;

public class Task {
    private String id;
    private String title;
    private String description;
    private String date;   // "yyyy-MM-dd"
    private String week;   // "yyyy-ww"
    private boolean done;
    private String type;   // "assignment", "exam", or "personal"
    private String priority;

    // Empty constructor required for Firestore
    public Task() {}

    // Getters
    public String getId() {
        return id;
    }


    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getWeek() {
        return week;
    }

    public boolean isDone() {
        return done;
    }

    public String getType() {
        return type;
    }

    public String getPriority() {
        return priority;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
