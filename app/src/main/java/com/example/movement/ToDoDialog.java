package com.example.movement;

import android.app.Dialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.movement.model.ToDo;

import java.util.Calendar;

public class ToDoDialog extends DialogFragment {

    private EditText taskNameInput;
    private Button saveButton;
    private AddToDoListener listener;

    public interface AddToDoListener {
        void onToDoAdded(String taskName); // Listener interface to send data back to HomeFragment
    }

    public void setAddToDoListener(AddToDoListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_todo, container, false);

        taskNameInput = view.findViewById(R.id.edit_text_task);
        saveButton = view.findViewById(R.id.button_save_task);

        saveButton.setOnClickListener(v -> {
            String taskName = taskNameInput.getText().toString();
            if (!taskName.isEmpty() && listener != null) {
                listener.onToDoAdded(taskName); // Send the task name to HomeFragment
                dismiss(); // Close the dialog after saving
            }
        });

        return view;
    }
}
