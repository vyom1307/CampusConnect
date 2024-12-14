package com.example.movement.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "todo_table")
public class ToDo {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String task;
    private String date; // Stores the date of the to-do item


    public ToDo(String task, String date) {
        this.task = task;
        this.date = date;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public String getDate() {
        return date;
    }


}
