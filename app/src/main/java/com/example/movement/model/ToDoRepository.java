package com.example.movement.model;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.movement.model.ToDo;
import com.example.movement.model.ToDoDao;
import com.example.movement.model.ToDoDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ToDoRepository {

    private ToDoDao toDoDao;
    private ExecutorService executorService;

    public ToDoRepository(Application application) {
        ToDoDatabase db = ToDoDatabase.getDatabase(application);
        toDoDao = db.toDoDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(final ToDo toDo) {
        executorService.execute(() -> toDoDao.insert(toDo));
    }

    public LiveData<List<ToDo>> getTasksByDate(String date) {
        return toDoDao.getTasksByDate(date);
    }
    public void delete(final ToDo task) {
        executorService.execute(() -> toDoDao.delete(task));
    }
}
