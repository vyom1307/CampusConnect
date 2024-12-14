package com.example.movement.ui.home;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movement.R;
import com.example.movement.ToDoAdapter;
import com.example.movement.ToDoDialog;
import com.example.movement.ToDoViewModel;
import com.example.movement.databinding.FragmentHomeBinding;
import com.example.movement.model.ToDo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ToDoViewModel toDoViewModel;
    private RecyclerView recyclerView;
    private ToDoAdapter toDoAdapter;
    private String selectedDate;
    private FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();


    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FirebaseUser user=firebaseAuth.getCurrentUser();

       String fullName = user.getDisplayName();
       String name = fullName.split(" ").length > 1 ? fullName.split(" ")[0] : fullName;
      name= name.toUpperCase();

        TextView greetings=root
                .findViewById(R.id.text_home);

        CalendarView calendarView = root.findViewById(R.id.calendarDate);
        calendarView.setDateTextAppearance(R.style.CalendarDateTextAppearance);


        BlurView blurView = root.findViewById(R.id.blurView); // Ensure correct ID
        setupBlurView(blurView, root);

        Calendar calendar = Calendar.getInstance();
        selectedDate = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

// Set background based on the time of day
        ImageView backgroundImageView = root.findViewById(R.id.background_image);
        // ConstraintLayout layout = root.findViewById(R.id.constraint); // Assuming your main layout is a RelativeLayout
        if (hourOfDay >= 5 && hourOfDay < 12) {
            backgroundImageView.setImageResource(R.drawable.sr2); // sunrise or morning theme
            greetings.setText("GM "+name+",");
        } else if (hourOfDay >= 12 && hourOfDay < 17) {
            backgroundImageView.setImageResource(R.drawable.after_noon); // afternoon theme
            greetings.setText("Hey "+name+",");
        } else if (hourOfDay >= 17 && hourOfDay < 20) {
            backgroundImageView.setImageResource(R.drawable.ss1); // sunset theme
            greetings.setText("Hey "+name+",");
        } else {
            backgroundImageView.setImageResource(R.drawable.t2); // night theme
            greetings.setText("GN "+name+",");
        }
        backgroundImageView.setAlpha(0.5f);

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault());
        String dateString = sdf.format(new Date());
        TextView date = root.findViewById(R.id.date);
        date.setText(dateString);

        toDoViewModel = new ViewModelProvider(this).get(ToDoViewModel.class);
        recyclerView = root.findViewById(R.id.todoRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        toDoAdapter = new ToDoAdapter(toDoViewModel);
        recyclerView.setAdapter(toDoAdapter);



        // Assuming you have a calendar or date picker to select the date
        // Placeholder: replace with selected date logic

        // Observe to-do list for the selected date




        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // Set the selected date in the format you want (e.g., YYYY-MM-DD)
            Log.d("DAAAATTTE SELECTED", "DATE I AM SELECTING IS " + year + "-" + (month + 1) + "-" + dayOfMonth);
            selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
            toDoViewModel.getTasksByDate(selectedDate).observe(getViewLifecycleOwner(), tasks -> {
                Log.d("ToDoList", "Tasks retrieved for date: " + selectedDate);
                for (ToDo task : tasks) {
                    Log.d("ToDoList", "Task: " + task.getTask() + " Date: " + task.getDate());
                }
                toDoAdapter.setToDoList(tasks);
                // Update the adapter with the tasks
            });
        });

        Button addTaskButton = root.findViewById(R.id.addtask);
        addTaskButton.setOnClickListener(v -> showAddTaskDialog());

        CardView cardView = root.findViewById(R.id.ToDoCV);
        cardView.setOnClickListener(v -> toogle());


        return root;
    }
    private void setupBlurView(BlurView blurView, View root) {
        float radius = 25f;

        // Use root.getRootView() to get the root layout of the fragment/activity
        ViewGroup rootView = (ViewGroup) root.getRootView();

        // Optional: Set background drawable
        Drawable windowBackground = blurView.getBackground();

        // Setting up the BlurView with RenderScriptBlur
        blurView.setupWith(rootView, new RenderScriptBlur(requireContext()))
                .setFrameClearDrawable(windowBackground) // Optional
                .setBlurRadius(radius);
    }

    private void toogle() {

        if (recyclerView.getVisibility() == View.VISIBLE) {
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

        private void showAddTaskDialog () {
            ToDoDialog dialogFragment = new ToDoDialog();

            dialogFragment.setAddToDoListener(new ToDoDialog.AddToDoListener() {
                @Override
                public void onToDoAdded(String taskName) {
                    if (taskName != null && !taskName.isEmpty()) {
                        // Create a new ToDo object with taskName and selectedDate
                        ToDo newToDo = new ToDo(taskName, selectedDate);

                        // Insert the new task into the ViewModel
                        toDoViewModel.insert(newToDo);
                    }
                }


            });

            dialogFragment.show(getParentFragmentManager(), "AddToDoDialogFragment");
        }
        @Override
        public void onDestroyView () {
            super.onDestroyView();
            binding = null;
        }
    }
