package com.example.movement.ui.outpass;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.movement.R;

import java.util.Calendar;
import java.util.Date;

public class Outpass extends Fragment {

    private OutpassViewModel mViewModel;

    public static Outpass newInstance() {
        return new Outpass();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_outpass, container, false);


        TextView returnDateInput = view.findViewById(R.id.return_date_input);

        returnDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize a Calendar instance to set the default date
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Create a DatePicker for the AlertDialog
                final DatePicker datePicker = new DatePicker(v.getContext());

                // Set the current date in the DatePicker as default
                datePicker.init(year, month, day, null);
                datePicker.setMinDate(System.currentTimeMillis() - 1000);

                // Build the AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setView(datePicker);
                builder.setTitle("Select Return Date");

                // Set the "OK" button and handle the selected date
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get the selected day, month, and year from DatePicker
                        int selectedDay = datePicker.getDayOfMonth();
                        int selectedMonth = datePicker.getMonth() + 1; // Month is 0-indexed
                        int selectedYear = datePicker.getYear();

                        // Format the date (e.g., YYYY-MM-DD)
                        String selectedDate = String.format("%02d-%02d-%04d",selectedDay,  selectedMonth ,selectedYear);

                        // Set the selected date in the TextView
                        returnDateInput.setText(selectedDate);
                    }
                });

                // Set the "Cancel" button to dismiss the dialog
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                // Show the AlertDialog
                builder.show();
            }
        });

        TextView leaveTimeInput = view.findViewById(R.id.leave_time_input);

        leaveTimeInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize a Calendar instance to get the current time
                final Calendar calendar = Calendar.getInstance();
                int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                int currentMinute = calendar.get(Calendar.MINUTE);

                // Create a TimePickerDialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Format the selected time (e.g., HH:MM)
                                String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                                // Set the selected time in the TextView
                                leaveTimeInput.setText(selectedTime);
                            }
                        }, currentHour, currentMinute, true); // true for 24-hour format

                // Show the TimePickerDialog
                timePickerDialog.show();
            }
        });




        return view;
    }


}