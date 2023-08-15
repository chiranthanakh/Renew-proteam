package com.proteam.renew.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.proteam.renew.R;

public class SplashActivity extends AppCompatActivity {

    ImageView splash_image;
    private static int SPLASH_TIME_OUT = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splash_image=findViewById(R.id.splash_image);
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_animation);
        splash_image.startAnimation(hyperspaceJumpAnimation);

        new Handler().postDelayed(new Runnable() {
            /* * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company*/

            @Override
            public void run() {

                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();

                }
        }, SPLASH_TIME_OUT);
    }
}