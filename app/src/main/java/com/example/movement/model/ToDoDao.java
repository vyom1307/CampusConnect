package com.example.movement.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ToDoDao {

    @Insert
    void insert(ToDo toDo);

    @Delete
    void delete(ToDo toDo);

    @Query("SELECT * FROM todo_table WHERE date = :date ORDER BY id DESC")
    LiveData<List<ToDo>> getTasksByDate(String date);

    @Query("DELETE FROM todo_table")
    void deleteAll();
}
