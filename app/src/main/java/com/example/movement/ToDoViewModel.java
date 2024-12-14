package com.example.movement;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.movement.model.ToDo;
import  com.example.movement.model.ToDoRepository;

import java.util.List;

public class ToDoViewModel extends AndroidViewModel {

    private ToDoRepository repository;
    private LiveData<List<ToDo>> allToDos;

    public ToDoViewModel(Application application) {
        super(application);
        repository = new ToDoRepository(application);

    }




    public void insert(ToDo toDo) {
        Log.d("DATAAAAAA", "onCreateView: "+ toDo.getTask() + " on " + toDo.getDate());
        repository.insert(toDo);
    }

    public void deleteTask(ToDo task) {
        repository.delete(task);
    }

    public LiveData<List<ToDo>> getTasksByDate(String date) {
        return repository.getTasksByDate(date);
    }
}
