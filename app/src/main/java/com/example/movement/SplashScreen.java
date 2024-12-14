package com.example.movement;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        View mainLayout = findViewById(R.id.main);
        ImageView logo = findViewById(R.id.logo);


        // Zoom in animation for the logo
        Animation zoomIn = AnimationUtils.loadAnimation(this, R.anim.finit_pulse);
        int colorFrom = Color.parseColor("#040823");
        int colorTo = Color.parseColor("#A4C9E6");
        Calendar cal= Calendar.getInstance();
        int hour=cal.get(Calendar.HOUR_OF_DAY);
        if(hour>6&&hour<12){

             colorTo = Color.parseColor("#44606C");
        }
        else if(hour>12&&hour<17){
            colorTo = Color.parseColor("#5992E0");
        }
        else if(hour>17&&hour<20){
            colorTo = Color.parseColor("#293565");
        }
        else{
            colorTo = Color.parseColor("#333333");
        }


        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(3000);
        colorAnimation.addUpdateListener(animator -> {

            mainLayout.setBackgroundColor((int) animator.getAnimatedValue());
        });

        // Start color animation along with zoom animation
        colorAnimation.start();
        logo.startAnimation(zoomIn);


        // After the logo animation ends, show clouds
        zoomIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {


                // Delay before moving to the home screen
                new Handler().postDelayed(() -> {
                    String role = getIntent().getStringExtra("role");
                    Intent intent = new Intent(SplashScreen.this, Homes.class);

                    intent.putExtra("role",role);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // Close splash activity
                }, 0); // Time clouds stay before transitioning
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

    }
}