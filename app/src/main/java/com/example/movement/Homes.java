package com.example.movement;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.movement.databinding.ActivityHomesBinding;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Objects;

public class Homes extends AppCompatActivity {


    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setSupportActionBar(binding.appBarHomes.toolbar);

//        binding.appBarHomes.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null)
//                        .setAnchorView(R.id.fab).show();
//            }
//        });


        NavigationView navigationView = binding.navView;

        Calendar cal= Calendar.getInstance();
        int hour=cal.get(Calendar.HOUR_OF_DAY);
        if(hour>6&&hour<12){
            binding.appBarHomes.toolbar.setTitle("Good Morning");
            navigationView.setBackgroundColor(Color.parseColor("#E9967D"));
        }
        else if(hour>12&&hour<17){
            binding.appBarHomes.toolbar.setTitle("Good Afternoon");
            navigationView.setBackgroundColor(Color.parseColor("#A4C9E6"));
        }
        else if(hour>17&&hour<20){
            binding.appBarHomes.toolbar.setTitle("Good Evening");
            navigationView.setBackgroundColor(Color.parseColor("#AB6556"));
        }
        else{
            binding.appBarHomes.toolbar.setTitle("Good Night");
            navigationView.setBackgroundColor(Color.parseColor("#333333"));

        }
        //333333
        OnBackPressedDispatcher onBackPressedDispatcher = new OnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        Intent intent=getIntent();
        String role=intent.getStringExtra("role");


        DrawerLayout drawer = binding.drawerLayout;

        Menu menu = navigationView.getMenu();
        if(role.equals("normal")){
            menu.findItem(R.id.nav_slideshow).setVisible(false);
            menu.findItem(R.id.nav_assign_complain).setVisible(false);

        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,R.id.nav_complaint,R.id.nav_assign_complain,R.id.nav_outpass)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_homes);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //updates header with profile pic and name
        updateHeader();

    }

    private void updateHeader() {
         String email=Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
       String name= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
        String url= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).toString();
       TextView nameView= binding.navView.getHeaderView(0).findViewById(R.id.name);
       TextView emailView= binding.navView.getHeaderView(0).findViewById(R.id.email);
        ImageView imageView= binding.navView.getHeaderView(0).findViewById(R.id.pic);
        Glide.with(this).load(url).into(imageView);
       nameView.setText(name);
       emailView.setText(email);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.homes, menu);

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_homes);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}