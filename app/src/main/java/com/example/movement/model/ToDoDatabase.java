package com.example.movement.model;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {ToDo.class}, version = 2)
public abstract class ToDoDatabase extends RoomDatabase {

    public abstract ToDoDao toDoDao();

    private static volatile ToDoDatabase INSTANCE;

    public static ToDoDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ToDoDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    ToDoDatabase.class, "todo_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}