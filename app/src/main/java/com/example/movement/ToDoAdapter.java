package com.example.movement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movement.model.ToDo;

import java.util.ArrayList;
import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ToDoHolder> {
    private List<ToDo> toDoList = new ArrayList<>();
    private  ToDoViewModel toDoViewModel;
    public ToDoAdapter(ToDoViewModel toDoViewModel) {
        this.toDoViewModel = toDoViewModel;
    }

    @NonNull
    @Override
    public ToDoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_list_item, parent, false);
        return new ToDoHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ToDoHolder holder, int position) {
        ToDo currentToDo = toDoList.get(position);
        holder.textViewTask.setText(currentToDo.getTask());


        // Handle delete button click
        holder.done.setOnClickListener(v -> {
            // Call the ViewModel to delete the task
            ToDo task=toDoList.get(position);
            toDoViewModel.deleteTask(task);
        });
    }

    @Override
    public int getItemCount() {
        return toDoList.size();
    }

    public void setToDoList(List<ToDo> toDos) {
        this.toDoList = toDos;
        notifyDataSetChanged();
    }

    class ToDoHolder extends RecyclerView.ViewHolder {
        private TextView textViewTask;
        private ImageView done;

        public ToDoHolder(View itemView) {
            super(itemView);
            textViewTask = itemView.findViewById(R.id.todo_text);
            done = itemView.findViewById(R.id.done);
        }
    }
}

